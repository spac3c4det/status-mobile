import pytest
from tests import marks
from tests.base_test_case import MultipleSharedDeviceTestCase, create_shared_drivers
from views.sign_in_view import SignInView
from views.chat_view import ChatView


@pytest.mark.xdist_group(name="one_3")
@marks.critical
class TestGroupChatMultipleDeviceMerged(MultipleSharedDeviceTestCase):

    def prepare_devices(self):
        self.drivers, self.loop = create_shared_drivers(3)
        self.message_before_adding = 'message before adding new user'
        self.message_to_admin = 'Hey, admin!'

        self.homes, self.public_keys, self.usernames, self.chats = {}, {}, {}, {}
        for key in self.drivers:
            sign_in = SignInView(self.drivers[key])
            self.homes[key] = sign_in.create_user(enable_notifications=True)
            SignInView(self.drivers[2]).put_app_to_background_and_back()
            self.public_keys[key], self.usernames[key] = sign_in.get_public_key_and_username(True)
            sign_in.home_button.click()
            SignInView(self.drivers[0]).put_app_to_background_and_back()
        self.chat_name = self.homes[0].get_random_chat_name()

        self.homes[0].just_fyi('Admin adds future members to contacts')
        for i in range(1, 3):
            self.homes[0].add_contact(self.public_keys[i])
            self.homes[0].home_button.double_click()

        self.homes[0].just_fyi('Member adds admin to contacts to see PNs and put app in background')
        self.homes[1].add_contact(self.public_keys[0])
        self.homes[1].home_button.double_click()

        self.homes[0].just_fyi('Admin creates group chat')
        self.chats[0] = self.homes[0].create_group_chat([self.usernames[1]], self.chat_name)
        for i in range(1, 3):
            self.chats[i] = ChatView(self.drivers[i])

        self.chats[0].send_message(self.message_before_adding)

    @marks.testrail_id(3994)
    def test_group_chat_push_system_messages_when_invited(self):
        self.homes[1].just_fyi("Check system messages in PNs")
        self.homes[2].put_app_to_background_and_back()
        self.homes[1].put_app_to_background()
        self.homes[1].open_notification_bar()
        pns = [self.chats[0].pn_invited_to_group_chat(self.usernames[0], self.chat_name),
               self.chats[0].pn_wants_you_to_join_to_group_chat(self.usernames[0], self.chat_name)]
        for pn in pns:
            if not self.homes[1].get_pn(pn):
                self.errors.append('%s is not shown after invite to group chat' % pn)
        if self.homes[1].get_pn(pns[0]):
            group_invite_pn = self.homes[1].get_pn(pns[0])
            group_invite_pn.click()
        else:
            self.homes[1].click_system_back_button(2)
            self.homes[1].get_chat(self.chat_name).click()

        self.homes[1].just_fyi("Check system messages in group chat for admin and member")
        create_system_message = self.chats[0].create_system_message(self.usernames[0], self.chat_name)
        has_added_system_message = self.chats[0].has_added_system_message(self.usernames[0], self.usernames[1])

        create_for_admin_system_message = 'You created the group %s' % self.chat_name
        joined_message = "You've joined %s from invitation by %s" % (self.chat_name, self.usernames[0])

        for message in [create_for_admin_system_message, create_system_message, has_added_system_message]:
            if not self.chats[0].element_by_text(message).is_element_displayed():
                self.errors.append('%s system message is not shown' % message)

        for message in [joined_message, create_system_message, has_added_system_message]:
            if not self.chats[1].element_by_text(message).is_element_displayed():
                self.errors.append('%s system message is not shown' % message)

        self.errors.verify_no_errors()

    @marks.testrail_id(700731)
    @marks.xfail(reason="test may fail as sometimes message 'Hey admin' is not delivered; needs investigation")
    def test_group_chat_join_send_text_messages_push(self):
        message_to_admin = self.message_to_admin
        [self.homes[i].home_button.double_click() for i in range(3)]
        self.homes[1].get_chat(self.chat_name).click()

        self.chats[1].send_message(message_to_admin)

        self.chats[0].just_fyi('check that PN is received and after tap you are redirected to group chat')
        self.chats[0].open_notification_bar()
        pn = self.homes[0].get_pn(message_to_admin)
        if pn:
            pn.click()
        else:
            self.homes[0].click_system_back_button()
            self.homes[0].get_chat(self.chat_name).click()

        self.chats[1].just_fyi('Check message status and message delivery')
        message_status = self.chats[1].chat_element_by_text(message_to_admin).status
        if message_status != 'delivered':
            self.errors.append('Message status is not delivered, it is %s!' % message_status)
        if not self.chats[0].chat_element_by_text(message_to_admin).is_element_displayed(30):
            self.drivers[0].fail('Message %s was not received by admin' % message_to_admin)
        self.errors.verify_no_errors()

    @marks.testrail_id(700732)
    def test_group_chat_add_new_member_activity_centre(self):
        [self.homes[i].home_button.double_click() for i in range(3)]
        self.homes[0].get_chat(self.chat_name).click()
        self.chats[0].add_members_to_group_chat([self.usernames[2]])

        self.chats[2].just_fyi("Check there will be no PN but unread in AC if got invite from non-contact")
        if not self.homes[2].notifications_unread_badge.is_element_displayed(60):
            self.drivers[2].fail("Group chat is not appeared in AC!")
        self.homes[2].open_notification_bar()
        if self.homes[2].element_by_text_part(self.usernames[0]).is_element_displayed():
            self.errors.append("PN about group chat invite is shown when invited by non-contact")

        self.homes[2].click_system_back_button()
        self.homes[2].get_chat(self.chat_name).click()

        for message in (self.message_to_admin, self.message_before_adding):
            if self.chats[2].chat_element_by_text(message).is_element_displayed():
                self.errors.append('%s is shown for new user' % message)
        self.errors.verify_no_errors()

    @marks.testrail_id(3998)
    @marks.xfail(reason="mysterious issue when PNs are not fetched from offline, can not reproduce on real devices; needs investigation")
    def test_group_chat_offline_pn(self):
        [self.homes[i].home_button.double_click() for i in range(3)]
        chat_name = 'for_offline_pn'
        self.homes[0].create_group_chat([self.usernames[1], self.usernames[2]], chat_name)
        self.homes[0].home_button.double_click()
        for i in range(1, 3):
            self.homes[i].get_chat(chat_name).click()

        message_1, message_2 = 'message from old member', 'message from new member'

        self.homes[0].just_fyi("Put admin device to offline and send messages from members")
        self.homes[0].toggle_airplane_mode()
        self.chats[1].send_message(message_1)
        self.chats[2].send_message(message_2)

        self.homes[0].just_fyi("Put admin device to online and check that messages and PNs will be fetched")
        self.homes[0].toggle_airplane_mode()
        self.homes[0].connection_offline_icon.wait_for_invisibility_of_element(60)
        self.homes[0].open_notification_bar()
        for message in (message_1, message_2):
            if not self.homes[0].element_by_text(message).is_element_displayed(30):
                self.errors.append('%s PN was not fetched from offline' % message)
        self.homes[0].click_system_back_button()
        self.homes[0].get_chat(chat_name).click()

        self.homes[0].just_fyi("check that messages are shown for every member")
        for i in range(3):
            for message in (message_1, message_2):
                if not self.chats[i].chat_element_by_text(message).is_element_displayed():
                    self.errors.append('%s if not shown for device %s' % (message, str(i)))
        self.errors.verify_no_errors()

    @marks.testrail_id(5756)
    def test_group_chat_highligted(self):
        chat_name = 'for_invited'
        [self.homes[i].home_button.double_click() for i in range(3)]
        self.homes[0].create_group_chat([self.usernames[1]], chat_name)

        self.homes[1].just_fyi("Check that new group chat from contact is highlited")
        chat_2_element = self.homes[1].get_chat(chat_name)
        if chat_2_element.no_message_preview.is_element_differs_from_template('highligted_preview_group.png', 0):
            self.errors.append("Preview message is not hightligted or text is not shown! ")
        chat_2 = self.homes[1].get_chat(chat_name).click()
        chat_2.home_button.click()
        if not chat_2_element.no_message_preview.is_element_differs_from_template('highligted_preview_group.png', 0):
            self.errors.append("Preview message is still hightligted after opening! ")

    @marks.testrail_id(3997)
    def test_group_chat_leave_relogin(self):
        left_system_message = self.chats[1].leave_system_message(self.usernames[0])
        self.drivers[2].quit()
        [self.homes[i].home_button.double_click() for i in range(2)]
        self.homes[0].home_button.double_click()
        self.homes[1].get_chat(self.chat_name).click()

        self.homes[0].just_fyi("Admin deleted chat via long press")
        self.homes[0].leave_chat_long_press(self.chat_name)

        self.homes[1].just_fyi('Check that leave system message is presented after user left the group chat')
        if not self.chats[1].chat_element_by_text(left_system_message).is_element_displayed():
            self.errors.append('System message when user leaves the chat is not shown')

        self.homes[0].just_fyi("Member sends some message, admin relogins and check chat does not reappear")
        self.chats[1].send_message(self.message_to_admin)
        self.homes[0].relogin()
        if self.homes[0].get_chat_from_home_view(self.chat_name).is_element_displayed():
            self.drivers[0].fail('Deleted %s is present after relaunch app' % self.chat_name)
