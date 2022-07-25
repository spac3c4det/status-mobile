(ns quo2.components.filter-tag
  (:require [quo2.foundations.colors :as colors]
            [quo.react-native :as rn]
            [quo.theme :as theme]
            [quo2.components.icon :as icons]
            [quo2.components.text :as text]))

(def themes {:light {:default  {:border-color     colors/neutral-20
                                :icon-color       colors/neutral-50
                                :text-color            {:style {:color colors/black}}}
                     :active   {:border-color     colors/neutral-30
                                :icon-color       colors/neutral-50
                                :label            {:style {:color colors/black}}}
                     :disabled {:border-color     colors/neutral-20
                                :icon-color       colors/neutral-50
                                :text-color            {:style {:color colors/black}}}}
             :dark  {:default  {:border-color     colors/neutral-70
                                :icon-color       colors/neutral-40
                                :text-color            {:style {:color colors/white}}}
                     :active   {:border-color     colors/neutral-60
                                :icon-color       colors/neutral-40
                                :text-color            {:style {:color colors/white}}}
                     :disabled {:border-color     colors/neutral-70
                                :icon-color       colors/neutral-40
                                :text-color            {:style {:color colors/white}}}}})

(defn style-container [size disabled border-color background-color label]
  (merge {:height             size
          :align-items        :center
          :justify-content    :center
          :flex-direction     :row
          :border-color       border-color
          :background-color   background-color
          :border-width       1
          :border-radius      size}
         (when disabled
           {:opacity 0.3})
         (if label
           {:padding-horizontal (case size 32 12 24 8)}
           {:width              size})))

(defn base-tag [_]
  (fn [{:keys [size text-color icon emoji icon-color disabled label border-color background-color]
        :or   {size 32}}]
    [rn/view {:style (style-container size disabled border-color background-color label)}
     (when icon
       [icons/icon icon {:container-style (when label
                                            {:margin-right 4})
                         :resize-mode      :center
                         :size             (case size
                                             32 20
                                             24 12)
                         :color            icon-color}])
     (when emoji
       [rn/image {:source emoji
                  :style  (merge (case size
                                   32 {:height 20
                                       :width  20}
                                   24 {:height 12
                                       :width  12})
                                 (when label
                                   {:margin-right 4}))}])
     (when label
       [rn/view
        [text/text (merge {:size            (case size
                                              32 :paragraph-1
                                              24 :paragraph-2
                                              20 :label nil)
                           :weight          :medium
                           :number-of-lines 1}
                          text-color)
         label]])]))

(defn filter-tag
  [_ _]
  (fn [{:keys [id on-press disabled size emoji icon active accessibility-label label]
        :or   {size 32}}]
    (let [state (cond disabled :disabled active :active :else :default)
          {:keys [icon-color border-color background-color text-color]}
          (get-in themes [(theme/get-theme) state])]
      [rn/touchable-without-feedback (merge {:disabled            disabled
                                             :accessibility-label accessibility-label}
                                            (when on-press
                                              {:on-press (fn []
                                                           (on-press id))}))
       [rn/view
        [base-tag {:size             size
                   :text-color       text-color
                   :icon             icon
                   :emoji            emoji
                   :icon-color       icon-color
                   :label            label
                   :border-color     border-color
                   :background-color background-color
                   :disabled         disabled}]]])))

