(ns status-im.ui.screens.chat.components.input
  (:require [status-im.ui.components.icons.icons :as icons]
            [quo.react-native :as rn]
            [oops.core :refer [oget]]
            [quo.react :as react]
            [quo.platform :as platform]
            [quo.components.text :as text]
            [quo.design-system.colors :as colors]
            [status-im.ui.screens.chat.components.style :as styles]
            [status-im.utils.fx :as fx]
            [status-im.ui.screens.chat.components.reply :as reply]
            [status-im.multiaccounts.core :as multiaccounts]
            [status-im.chat.constants :as chat.constants]
            [status-im.utils.utils :as utils.utils]
            [quo.components.animated.pressable :as pressable]
            [re-frame.core :as re-frame]
            [status-im.i18n.i18n :as i18n]
            [status-im.chat.models.mentions :as mentions]
            [status-im.ui.components.list.views :as list]
            [quo.components.list.item :as list-item]
            [status-im.ui.screens.chat.photos :as photos]
            [reagent.core :as reagent]
            [clojure.string :as string]
            [quo2.components.button :as quo2]
            [quo2.reanimated :as reanimated]
            [quo2.gesture :as gesture]
            [quo.components.safe-area :as safe-area]))

(defn input-focus [text-input-ref]
  (some-> ^js (react/current-ref text-input-ref) .focus))

(def panel->icons {:extensions :main-icons/commands
                   :images     :main-icons/photo})

(defn touchable-icon [{:keys [panel active set-active accessibility-label]}]
  [pressable/pressable {:type                :scale
                        :accessibility-label accessibility-label
                        :on-press            #(set-active (when-not (= active panel) panel))}
   [rn/view {:style (styles/touchable-icon)}
    [icons/icon
     (panel->icons panel)
     (styles/icon (= active panel))]]])

(defn touchable-stickers-icon [{:keys [panel active set-active accessibility-label input-focus]}]
  [pressable/pressable {:type                :scale
                        :accessibility-label accessibility-label
                        :on-press            #(if (= active panel)
                                                (input-focus)
                                                (set-active panel))}
   [rn/view {:style (styles/in-input-touchable-icon)}
    (if (= active panel)
      [icons/icon :main-icons/keyboard (styles/icon false)]
      [icons/icon :main-icons/stickers (styles/icon false)])]])

;; TODO(Ferossgp): Move this into audio panel.
;; Instead of not changing panel we can show a placeholder with no permission
(defn- request-record-audio-permission [set-active panel]
  (re-frame/dispatch
   [:request-permissions
    {:permissions [:record-audio]
     :on-allowed
     #(set-active panel)
     :on-denied
     #(utils.utils/set-timeout
       (fn []
         (utils.utils/show-popup (i18n/label :t/audio-recorder-error)
                                 (i18n/label :t/audio-recorder-permissions-error)))
       50)}]))

(defn touchable-audio-icon [{:keys [panel active set-active accessibility-label input-focus]}]
  [pressable/pressable {:type                :scale
                        :accessibility-label accessibility-label
                        :on-press            #(if (= active panel)
                                                (input-focus)
                                                (request-record-audio-permission set-active panel))}
   [rn/view {:style (styles/in-input-touchable-icon)}
    (if (= active panel)
      [icons/icon :main-icons/keyboard (styles/icon false)]
      [icons/icon :main-icons/speech (styles/icon false)])]])

(defn send-button [on-send contact-request]
  [rn/touchable-opacity {:on-press-in on-send}
   [rn/view {:style (styles/send-message-button)}
    (when-not contact-request
      [icons/icon :main-icons/arrow-up
       {:container-style     (styles/send-message-container contact-request)
        :accessibility-label :send-message-button
        :color               (styles/send-icon-color)}])]])

(defn send-button-old [on-send contact-request]
  [rn/touchable-opacity {:on-press-in on-send}
   [rn/view {:style (styles/send-message-button)}
    (when-not contact-request
      [icons/icon :main-icons/arrow-up
       {:container-style     (styles/send-message-container contact-request)
        :accessibility-label :send-message-button
        :color               (styles/send-icon-color)}])]])

(defn on-selection-change [timeout-id last-text-change mentionable-users args]
  (let [selection (.-selection ^js (.-nativeEvent ^js args))
        start (.-start selection)
        end (.-end selection)]
    ;; NOTE(rasom): on iOS we do not dispatch this event immediately
    ;; because it is needed only in case if selection is changed without
    ;; typing. Timeout might be canceled on `on-change`.
    (when platform/ios?
      (reset!
       timeout-id
       (utils.utils/set-timeout
        #(re-frame/dispatch [::mentions/on-selection-change
                             {:start start
                              :end   end}
                             mentionable-users])
        50)))
    ;; NOTE(rasom): on Android we dispatch event only in case if there
    ;; was no text changes during last 50ms. `on-selection-change` is
    ;; dispatched after `on-change`, that's why there is no another way
    ;; to know whether selection was changed without typing.
    (when (and platform/android?
               (or (not @last-text-change)
                   (< 50 (- (js/Date.now) @last-text-change))))
      (re-frame/dispatch [::mentions/on-selection-change
                          {:start start
                           :end   end}
                          mentionable-users]))))

(defonce input-texts (atom {}))
(defonce mentions-enabled (reagent/atom {}))
(defonce chat-input-key (reagent/atom 1))

(re-frame/reg-fx
 :chat.ui/clear-inputs
 (fn []
   (reset! input-texts {})
   (reset! mentions-enabled {})
   (reset! chat-input-key 1)))

(defn force-text-input-update!
  "force-text-input-update! forces the
  input to re-render, necessary when we are setting value"
  []
  (swap! chat-input-key inc))

(defn show-send [{:keys [actions-ref send-ref sticker-ref]}]
  ;(quo.react/set-native-props actions-ref #js {:width 0 :left -88})
  (quo.react/set-native-props send-ref #js {:width nil :right nil}))
  ;(quo.react/set-native-props sticker-ref #js {:width 0 :right -100}))

(defn hide-send [{:keys [actions-ref send-ref sticker-ref]}]
  ;(quo.react/set-native-props actions-ref #js {:width nil :left nil})
  (quo.react/set-native-props send-ref #js {:width 0 :right -100}))
  ;(quo.react/set-native-props sticker-ref #js {:width nil :right nil}))

(defn reset-input [refs chat-id]
  (some-> ^js (react/current-ref (:text-input-ref refs)) .clear)
  (swap! mentions-enabled update :render not)
  (swap! input-texts dissoc chat-id))

(defn clear-input [chat-id refs]
  (hide-send refs)
  (if (get @mentions-enabled chat-id)
    (do
      (swap! mentions-enabled dissoc chat-id)
      ;;we need this timeout, because if we clear text input and first index was a mention object with blue color,
      ;;after clearing text will be typed with this blue color, so we render white text first and then clear it
      (js/setTimeout #(reset-input refs chat-id) 50))
    (reset-input refs chat-id)))

(defn on-text-change [val chat-id]
  (swap! input-texts assoc chat-id val)
  ;;we still store it in app-db for mentions, we don't have reactions in views
  (re-frame/dispatch [:chat.ui/set-chat-input-text val]))

(defn on-change [last-text-change timeout-id mentionable-users refs chat-id sending-image args]
  (let [text (.-text ^js (.-nativeEvent ^js args))
        prev-text (get @input-texts chat-id)]
    (when (and (seq prev-text) (empty? text) (not sending-image))
      (hide-send refs))
    (when (and (empty? prev-text) (seq text))
      (show-send refs))

    (when (and (not (get @mentions-enabled chat-id)) (string/index-of text "@"))
      (swap! mentions-enabled assoc chat-id true))

    ;; NOTE(rasom): on iOS `on-selection-change` is canceled in case if it
    ;; happens during typing because it is not needed for mention
    ;; suggestions calculation
    (when (and platform/ios? @timeout-id)
      (utils.utils/clear-timeout @timeout-id))
    (when platform/android?
      (reset! last-text-change (js/Date.now)))

    (on-text-change text chat-id)
    ;; NOTE(rasom): on iOS `on-change` is dispatched after `on-text-input`,
    ;; that's why mention suggestions are calculated on `on-change`
    (when platform/ios?
      (re-frame/dispatch [::mentions/calculate-suggestions mentionable-users]))))

(re-frame/reg-fx
 :set-input-text
 (fn [[chat-id text]]
   ;; We enable mentions
   (swap! mentions-enabled assoc chat-id true)
   (on-text-change text chat-id)
   ;; We update the key so that we force a refresh of the text input, as those
   ;; are not ratoms
   (force-text-input-update!)))

(fx/defn set-input-text
  "Set input text for current-chat. Takes db and input text and cofx
  as arguments and returns new fx. Always clear all validation messages."
  {:events [:chat.ui.input/set-chat-input-text]}
  [{:keys [db] :as cofx} text chat-id]
  (let [text-with-mentions (mentions/->input-field text)
        contacts (:contacts db)
        hydrated-mentions (map (fn [[t mention :as e]]
                                 (if (= t :mention)
                                   [:mention (str "@" (multiaccounts/displayed-name
                                                       (or (get contacts mention)
                                                           {:public-key mention})))]
                                   e)) text-with-mentions)
        info (mentions/->info hydrated-mentions)]
    {:set-input-text [chat-id text]
     :db
     (-> db
         (assoc-in [:chats/cursor chat-id] (:mention-end info))
         (assoc-in [:chat/inputs-with-mentions chat-id] hydrated-mentions)
         (assoc-in [:chats/mentions chat-id :mentions] info))}))

(defn on-text-input [mentionable-users chat-id args]
  (let [native-event (.-nativeEvent ^js args)
        text (.-text ^js native-event)
        previous-text (.-previousText ^js native-event)
        range (.-range ^js native-event)
        start (.-start ^js range)
        end (.-end ^js range)]
    (when (and (not (get @mentions-enabled chat-id)) (string/index-of text "@"))
      (swap! mentions-enabled assoc chat-id true))

    (re-frame/dispatch
     [::mentions/on-text-input
      {:new-text      text
       :previous-text previous-text
       :start         start
       :end           end}])
    ;; NOTE(rasom): on Android `on-text-input` is dispatched after
    ;; `on-change`, that's why mention suggestions are calculated
    ;; on `on-change`
    (when platform/android?
      (re-frame/dispatch [::mentions/calculate-suggestions mentionable-users]))))

(defn text-input-old [{:keys [set-active-panel refs chat-id sending-image]}]
  (let [cooldown-enabled? @(re-frame/subscribe [:chats/cooldown-enabled?])
        mentionable-users @(re-frame/subscribe [:chats/mentionable-users])
        timeout-id (atom nil)
        last-text-change (atom nil)
        mentions-enabled (get @mentions-enabled chat-id)
        contact-request @(re-frame/subscribe [:chats/sending-contact-request])]

    [rn/text-input
     {:style                    (styles/text-input-old contact-request)
      :ref                      (:text-input-ref refs)
      :max-font-size-multiplier 1
      :accessibility-label      :chat-message-input
      :text-align-vertical      :center
      :multiline                true
      :editable                 (not cooldown-enabled?)
      :blur-on-submit           false
      :auto-focus               false
      :on-focus                 #(set-active-panel nil)
      :max-length               chat.constants/max-text-size
      :placeholder-text-color   (:text-02 @colors/theme)
      :placeholder              (if cooldown-enabled?
                                  (i18n/label :cooldown/text-input-disabled)
                                  (i18n/label :t/type-a-message))
      :underline-color-android  :transparent
      :auto-capitalize          :sentences
      :on-selection-change      (partial on-selection-change timeout-id last-text-change mentionable-users)
      :on-change                (partial on-change last-text-change timeout-id mentionable-users refs chat-id sending-image)
      :on-text-input            (partial on-text-input mentionable-users chat-id)}
     (if mentions-enabled
       (for [[idx [type text]] (map-indexed
                                 (fn [idx item]
                                   [idx item])
                                 @(re-frame/subscribe [:chat/input-with-mentions]))]
         ^{:key (str idx "_" type "_" text)}
         [rn/text (when (= type :mention) {:style {:color "#0DA4C9"}})
          text])
       (get @input-texts chat-id))]))

(defn text-input [{:keys [set-active-panel refs chat-id sending-image on-content-size-change]}]
  (let [cooldown-enabled? @(re-frame/subscribe [:chats/cooldown-enabled?])
        mentionable-users @(re-frame/subscribe [:chats/mentionable-users])
        timeout-id (atom nil)
        last-text-change (atom nil)
        mentions-enabled (get @mentions-enabled chat-id)]

    [rn/text-input
     {:style                    (styles/text-input)
      :ref                      (:text-input-ref refs)
      :max-font-size-multiplier 1
      :accessibility-label      :chat-message-input
      :text-align-vertical      :center
      :multiline                true
      :editable                 (not cooldown-enabled?)
      :blur-on-submit           false
      :auto-focus               false
      :on-focus                 #(set-active-panel nil)
      :max-length               chat.constants/max-text-size
      :placeholder-text-color   (:text-02 @colors/theme)
      :placeholder              (if cooldown-enabled?
                                  (i18n/label :cooldown/text-input-disabled)
                                  (i18n/label :t/type-a-message))
      :underline-color-android  :transparent
      :auto-capitalize          :sentences
      :onContentSizeChange      on-content-size-change
      :on-selection-change      (partial on-selection-change timeout-id last-text-change mentionable-users)
      :on-change                (partial on-change last-text-change timeout-id mentionable-users refs chat-id sending-image)
      :on-text-input            (partial on-text-input mentionable-users chat-id)}
     (if mentions-enabled
       (for [[idx [type text]] (map-indexed
                                (fn [idx item]
                                  [idx item])
                                @(re-frame/subscribe [:chat/input-with-mentions]))]
         ^{:key (str idx "_" type "_" text)}
         [rn/text (when (= type :mention) {:style {:color "#0DA4C9"}})
          text])
       (get @input-texts chat-id))]))

(defn mention-item
  [[public-key {:keys [alias name nickname] :as user}] _ _ text-input-ref]
  (let [ens-name? (not= alias name)]
    [list-item/list-item
     (cond-> {:icon              [photos/member-photo public-key]
              :size              :small
              :text-size         :small
              :title
              [text/text
               {:weight          :medium
                :ellipsize-mode  :tail
                :number-of-lines 1
                :size            :small}
               (if nickname
                 nickname
                 name)
               (when nickname
                 [text/text
                  {:weight         :regular
                   :color          :secondary
                   :ellipsize-mode :tail
                   :size           :small}
                  " "
                  (when ens-name?
                    "@")
                  name])]
              :title-text-weight :medium
              :on-press
              (fn []
                (re-frame/dispatch [:chat.ui/select-mention text-input-ref user]))}

       ens-name?
       (assoc :subtitle alias))]))

(def chat-toolbar-height (reagent/atom nil))

(defn autocomplete-mentions [text-input-ref bottom]
  (let [suggestions @(re-frame/subscribe [:chat/mention-suggestions])]
    (when (seq suggestions)
      (let [height (+ 16 (* 52 (min 4.5 (count suggestions))))]
        [rn/view
         {:style               (styles/autocomplete-container bottom)
          :accessibility-label :suggestions-list}
         [rn/view
          {:style {:height height}}
          [list/flat-list
           {:keyboardShouldPersistTaps :always
            :footer                    [rn/view {:style {:height 8}}]
            :header                    [rn/view {:style {:height 8}}]
            :data                      suggestions
            :key-fn                    first
            :render-data               text-input-ref
            :render-fn                 mention-item}]]]))))

(defn on-chat-toolbar-layout [^js ev]
  (reset! chat-toolbar-height (-> ev .-nativeEvent .-layout .-height)))

(defn send-image []
  (let [sending-image @(re-frame/subscribe [:chats/sending-image])]
    (when (seq sending-image)
      [reply/send-image sending-image])))

(defn actions [extensions image show-send actions-ref active-panel set-active-panel contact-request]
  [rn/view {:style (styles/actions-wrapper (and (not contact-request) show-send))
            :ref   actions-ref}
   (when extensions
     [touchable-icon {:panel               :extensions
                      :accessibility-label :show-extensions-icon
                      :active              active-panel
                      :set-active          set-active-panel}])
   (when image
     [touchable-icon {:panel               :images
                      :accessibility-label :show-photo-icon
                      :active              active-panel
                      :set-active          set-active-panel}])])

(defn chat-toolbar-old []
  (let [actions-ref (quo.react/create-ref)
        send-ref (quo.react/create-ref)
        sticker-ref (quo.react/create-ref)
        toolbar-options (re-frame/subscribe [:chats/chat-toolbar])]
    (fn [{:keys [active-panel set-active-panel text-input-ref chat-id]}]
      (let [;we want to control components on native level, so instead of RN state we set native props via reference
            ;we don't react on input text in this view, @input-texts below is a regular atom
            refs {:actions-ref    actions-ref
                  :send-ref       send-ref
                  :sticker-ref    sticker-ref
                  :text-input-ref text-input-ref}
            {:keys [send stickers image extensions audio sending-image]} @toolbar-options
            show-send (or sending-image (seq (get @input-texts chat-id)))
            contact-request @(re-frame/subscribe [:chats/sending-contact-request])]
        [rn/view {:style     (styles/toolbar)
                  :on-layout on-chat-toolbar-layout}
           ;;EXTENSIONS and IMAGE buttons
         [actions extensions image show-send actions-ref active-panel set-active-panel contact-request]
         [rn/view {:style (styles/input-container contact-request)}
          [send-image]
          [rn/view {:style styles/input-row}
           [text-input-old {:chat-id          chat-id
                            :sending-image    sending-image
                            :refs             refs
                            :set-active-panel set-active-panel}]
           ;;SEND button
           [rn/view {:ref send-ref :style (when-not show-send {:width 0 :right -100})}
            (when send
              [send-button-old #(do (clear-input chat-id refs)
                                    (re-frame/dispatch [:chat.ui/send-current-message]))
               contact-request])]

           ;;STICKERS and AUDIO buttons
           (when-not @(re-frame/subscribe [:chats/edit-message])
             [rn/view {:style (merge {:flex-direction :row} (when show-send {:width 0 :right -100}))
                       :ref   sticker-ref}
              (when stickers
                [touchable-stickers-icon {:panel               :stickers
                                          :accessibility-label :show-stickers-icon
                                          :active              active-panel
                                          :input-focus         #(input-focus text-input-ref)
                                          :set-active          set-active-panel}])
              (when audio
                [touchable-audio-icon {:panel               :audio
                                       :accessibility-label :show-audio-message-icon
                                       :active              active-panel
                                       :input-focus         #(input-focus text-input-ref)
                                       :set-active          set-active-panel}])])]]]))))

;;:state - :min, :custom-chat-available,  :custom-chat-unavailable,  :max
(defn chat-input-bottom-sheet [chat-id text-input-ref]
  [safe-area/consumer
   (fn [insets]
     (let [min-y 108
           context (atom {:y min-y
                          :min-y min-y
                          :dy 0
                          :pdy 0
                          :state :min})]
       (fn []
         [:f>
          (fn []
            (let [send-ref (quo.react/create-ref)
                  {window-height :height} (rn/use-window-dimensions)
                  keyboard-was-shown (atom false)
                  {:keys [keyboard-shown
                          keyboard-height]} (rn/use-keyboard)
                  max-y (- window-height (if (> keyboard-height 0) keyboard-height 360 ) (:top insets))
                  y (if keyboard-shown
                      (if (= (:state @context) :max)
                        max-y
                        (if (< (:y @context) max-y)
                          (:y @context)
                          (do
                            (swap! context assoc :state :max)
                            max-y)))
                      min-y)
                  refs {;:actions-ref    actions-ref
                        :send-ref       send-ref
                        ;:sticker-ref    sticker-ref
                        :text-input-ref text-input-ref}
                  translate-y (reanimated/use-shared-value 0)
                  shared-height (reanimated/use-shared-value min-y)
                  bottom-sheet-gesture (-> (gesture/gesture-pan)
                                           (gesture/on-start
                                             (fn [_]
                                               (if keyboard-shown
                                                 (swap! context assoc :pan-y (reanimated/get-shared-value translate-y))
                                                 (input-focus text-input-ref))))
                                           (gesture/on-update
                                             (fn [evt]
                                               (when keyboard-shown
                                                 (swap! context assoc :dy (- (.-translationY evt) (:pdy @context)))
                                                 (swap! context assoc :pdy (.-translationY evt))
                                                 (reanimated/set-shared-value
                                                   translate-y
                                                   (max (min (+ (.-translationY evt) (:pan-y @context)) (- min-y)) (- max-y)))
                                                 (println (.-velocityY evt) (+ (.-translationY evt) (:pan-y @context)) window-height))))
                                           (gesture/on-end
                                             (fn [evt]
                                               (when keyboard-shown
                                                 (if (<  (:dy @context) 0)
                                                   (do
                                                     (swap! context assoc :state :max)
                                                     (input-focus text-input-ref)
                                                     (reanimated/set-shared-value translate-y (reanimated/with-timing (- max-y))))
                                                   (do
                                                     (swap! context assoc :state :min)
                                                     (reanimated/set-shared-value translate-y (reanimated/with-timing (- min-y)))
                                                     (re-frame/dispatch [:dismiss-keyboard])))))))
                  input-content-change (fn [evt]
                                         (when (not= (:state @context) :max)
                                           (let [new-y (+ min-y (- (max (oget evt "nativeEvent" "contentSize" "height") 22) 22))]
                                             (println "CONT"  (oget evt "nativeEvent" "contentSize" "height") new-y max-y)
                                             (if (< new-y max-y)
                                               (do
                                                 (swap! context assoc :state :custom-chat-available)
                                                 (swap! context assoc :y new-y)
                                                 (when keyboard-shown
                                                   (reanimated/set-shared-value
                                                     translate-y
                                                     (reanimated/with-timing (- new-y)))
                                                   (reanimated/set-shared-value
                                                     shared-height
                                                     (reanimated/with-timing new-y))))
                                               (do
                                                 (swap! context assoc :state :max)
                                                 (swap! context assoc :y max-y)
                                                 (when keyboard-shown
                                                   (reanimated/set-shared-value
                                                     translate-y
                                                     (reanimated/with-timing (- max-y)))))))))]
              (quo.react/effect! #(do
                                    (println "EFFECT" @keyboard-was-shown keyboard-shown (:state @context) (:y @context) y)
                                    (when (and @keyboard-was-shown (not keyboard-shown))
                                      (swap! context assoc :state :min))
                                    (reset! keyboard-was-shown keyboard-shown)
                                    (reanimated/set-shared-value translate-y (reanimated/with-timing (- y)))
                                    (reanimated/set-shared-value shared-height (reanimated/with-timing y))))
              [reanimated/view {:style (reanimated/apply-animations-to-style
                                         {:height shared-height}
                                         {})}
                                          ;:border-color :red :border-width 1})}

               [gesture/gesture-detector {:gesture bottom-sheet-gesture}
                [reanimated/view {:style (reanimated/apply-animations-to-style
                                           {:transform [{:translateY translate-y}]}
                                           {:border-top-left-radius 20 :border-top-right-radius 20
                                            :position :absolute :left 0 :right 0 :bottom (- window-height)
                                            :height window-height
                                            :flex 1
                                            :background-color :white
                                            :shadow-radius  16
                                            :shadow-opacity 1
                                            :shadow-color   "rgba(9, 16, 28, 0.04)"
                                            :shadow-offset  {:width 0 :height -2}
                                            :elevation 2
                                            :z-index 1000})}
                 [rn/view
                  {:width            32
                   :height           4
                   :background-color :black
                   :opacity          0.05
                   :border-radius    100
                   :align-self :center
                   :margin-top 8}]
                 [rn/view {:style {:height (- max-y 80)}}
                  [text-input {:chat-id          chat-id
                               :on-content-size-change input-content-change
                               :sending-image    false
                               :refs             refs
                               :set-active-panel #()}]]]]
               [rn/view {:flex-direction :row :padding-horizontal 20 :padding-top 12
                         :elevation 2
                         :z-index 2000
                         :padding-bottom (+ 12 (:bottom insets))
                         ;:border-color :blue :border-width 1
                         :position :absolute :background-color :white
                         ;;TODO on ios when keyboard is opens we need -5 dunno why, but there is a shift for some reason
                         :bottom (- -12 (:bottom insets) -12)}
                [quo2/button {:icon true :type :outline :size 32} :main-icons2/image]
                [rn/view {:width 12}]
                [quo2/button {:icon true :type :outline :size 32} :main-icons2/reaction]
                [rn/view {:flex 1}]
                ;;SEND button
                [rn/view {:ref send-ref :style (when-not show-send {:width 0 :right -100})}
                 [quo2/button {:icon true :size 32 :accessibility-label :send-message-button
                               :on-press #(do (clear-input chat-id refs)
                                              (re-frame/dispatch [:chat.ui/send-current-message]))}
                  :main-icons2/arrow-up]]]]))])))])