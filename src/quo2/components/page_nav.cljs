(ns quo2.components.page-nav
  (:require [clojure.string :as string]
            [quo.react-native :as rn]
            [quo2.foundations.colors :as colors]
            [status-im.ui.components.icons.icons :as icons]
            [status-im.utils.dimensions :as dimensions]))

(def centrify
  {:display :flex
   :justify-content :center
   :align-items :center})

(defn mid-section
[{:keys [mid-section-type mid-section-main-text mid-section-right-icon mid-section-icon mid-section-main-text-icon-color mid-section-left-icon]}]
[rn/view {:id "mid"
          :style (merge
                  centrify
                  {:flex 1})}
 (case mid-section-type
   :text-only [rn/text mid-section-main-text]
   :text-with-two-icons [rn/view (assoc
                                  centrify
                                  :flex-direction :row)
                         [icons/icon (keyword "main-icons"
                                              (name mid-section-left-icon))
                          {:width 16
                           :height 16
                           :color mid-section-main-text-icon-color}]
                         [rn/text {:style
                                   {:padding-horizontal 4
                                    :font-size 15
                                    :font-weight "600"}}
                          mid-section-main-text]
                         [rn/view {:style {:font-size 15}}
                          [icons/icon (keyword "main-icons"
                                               (name mid-section-right-icon))
                           {:width 16
                            :height 16
                            :color mid-section-main-text-icon-color}]]]
   :text-with-one-icon [rn/view (assoc centrify
                                       :flex-direction :row)
                        [rn/text {:style
                                  {:padding-horizontal 4
                                   :font-size 15
                                   :font-weight "600"}}
                         mid-section-main-text]
                        [rn/view {:style {:font-size 15}}
                         [icons/icon (keyword "main-icons" (name mid-section-icon))
                          {:width 16
                           :height 16
                           :color mid-section-main-text-icon-color}]]])])

(def icon-styles (merge
                  centrify
                  {:width 32
                   :height 32
                   :border-radius 10}))

(defn page-nav
  [{:keys [left-section mid-section right-section opts] :or {opts {:align-mid :none
                                                                   :page-nav-color "red"
                                                                   :page-nav-background-uri ""}
                                                             mid-section {:mid-section-type :text-with-two-icons
                                                                          :mid-section-icon :wallet
                                                                          :mid-section-main-text "# general"
                                                                          :mid-section-left-icon :wallet 
                                                                          :mid-section-right-icon :wallet
                                                                          :mid-section-main-text-icon :wallet
                                                                          :mid-section-main-text-icon-color "nil"
                                                                          :mid-section-sub-text ""
                                                                          :mid-section-sub-text-icon :locked
                                                                          :mid-section-user-icon-uri "wow"
                                                                          :mid-section-align-text :row}
                                                             left-section {:left-section-icon :peach20
                                                                           :left-section-icon-color "none"
                                                                           :left-section-icon-bg-color colors/neutral-30}
                                                             right-section {:right-section-icons [{:icon-bg-color colors/primary-40
                                                                                                   :icon-color "none"
                                                                                                   :icon-name "peach"}]}}}]
  (let [{:keys [height width]} (dimensions/window)
        put-middle-section-on-left? (or (= (:align-mid opts)
                                           :left)
                                        (> (count right-section) 1))
        _ (prn put-middle-section-on-left?)
        {:keys [color
                page-nav-background-uri
                page-nav-color]} opts
        {:keys [left-section-icon left-section-icon-color left-section-icon-bg-color]} left-section
        right-icons (:right-section-icons right-section)
        {:keys [mid-section-type mid-section-icon
                mid-section-main-text mid-section-text-icon
                mid-section-main-text-icon-color
                mid-section-left-icon
                mid-section-right-icon
                mid-section-sub-text mid-section-sub-text-icon
                mid-section-user-icon-url mid-section--align-text]} mid-section]
    [rn/view {:style
              (cond->
               {:display :flex
                :flex-direction :row
                :width width
                :height (* 0.075 height)
                :align-items :center
                :padding-horizontal 8
                :justify-content :space-between}
                (string/blank? page-nav-background-uri) (assoc :background-color page-nav-color)
                (string/blank? page-nav-color) (assoc :background page-nav-background-uri))}
     [rn/view {:id "leftsection"
               :style {:flex 1}}
      [rn/view {:style (merge
                        icon-styles
                        {:background-color left-section-icon-bg-color})}
       [icons/icon :main-icons/placeholder20 {:color "nil"}]]
      (when put-middle-section-on-left?
        [mid-section {:mid-section-type mid-section-type
                      :mid-section-main-text mid-section-main-text
                      :mid-section-right-icon mid-section-right-icon
                      :mid-section-icon mid-section-icon
                      :mid-section-main-text-icon-color mid-section-main-text-icon-color
                      :mid-section-left-icon mid-section-left-icon}])]
     (when-not put-middle-section-on-left?
       [mid-section {:mid-section-type mid-section-type
                     :mid-section-main-text mid-section-main-text
                     :mid-section-right-icon mid-section-right-icon
                     :mid-section-icon mid-section-icon
                     :mid-section-main-text-icon-color mid-section-main-text-icon-color
                     :mid-section-left-icon mid-section-left-icon}])
     [rn/view {:id "rightsection"
               :style (assoc
                       (merge
                        centrify
                        {:flex-direction :row
                         :flex 1})
                       :justify-content :flex-end)}
      [rn/view {:style (merge
                        icon-styles
                        {:background-color colors/neutral-10
                         :margin-right 8})}
       [icons/icon :main-icons/placeholder20 {:color "nil"}]]
      [rn/view {:style (merge
                        icon-styles
                        {:background-color colors/neutral-10})}
       [icons/icon :main-icons/placeholder20 {:color "nil"}]]]]))