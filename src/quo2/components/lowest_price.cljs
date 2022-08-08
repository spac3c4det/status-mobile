(ns quo2.components.lowest-price
  (:require [quo.design-system.colors :as colors]
            [quo.react-native :as rn]))

(defn lowest-price
  [{:keys [top-value
           bottom-value
           margin-top
           top-value-text-color
           top-value-bg-color
           bottom-value-bg-color
           bottom-value-text-color]}]
  [rn/view {:style {:flex-direction :column
                    :width "100%"
                    :margin-top (int margin-top)
                    :justify-content :center}}
   [rn/view {:style
             {:flex-direction :row
              :justify-content :center
              :align-items :center
              :overflow :hidden}}
    [rn/view {:style {:opacity 0.4
                      :border-style :dotted
                      :border-width 1
                      :border-color (:text-01 @colors/theme)
                      :flex-grow 1
                      :overflow :hidden
                      :border-radius 1}}]
    [rn/view {:style {:align-self :flex-start
                      :padding-horizontal 2
                      :padding-vertical 2
                      :flex-grow 0.25
                      :border-radius 3
                      :background-color top-value-bg-color}}
     [rn/text {:style {:color top-value-text-color
                       :text-align :center}} top-value]]
    [rn/view {:style
              {:opacity 0.4
               :border-style :dotted
               :border-width 1
               :border-color (:text-01 @colors/theme)
               :flex-grow 4
               :overflow :hidden
               :border-radius 1}}]]
   [rn/view {:style
             {:flex-direction :row
              :justify-content :center
              :align-items :center}}
    [rn/view
     {:style
      {:opacity 0.4
       :border-style :dotted
       :border-width 1
       :border-color (:text-01 @colors/theme)
       :flex-grow 4
       :overflow :hidden
       :border-radius 1}}]
    [rn/view {:style {:align-self :flex-start
                      :padding-horizontal 2
                      :padding-vertical 2
                      :flex-grow 0.25
                      :border-radius 3
                      :background-color bottom-value-bg-color
                      :border-top-color "black"}}
     [rn/text {:style {:color bottom-value-text-color
                       :text-align :center}} bottom-value]]
    [rn/view {:style
              {:opacity 0.4
               :border-style :dotted
               :border-width 1
               :border-color (:text-01 @colors/theme)
               :flex-grow 1
               :overflow :hidden
               :border-radius 1}}]]])