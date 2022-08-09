(ns quo2.components.network-breakdown
  (:require [quo.core :as quo]
            [status-im.ui.components.icons.icons :as icons]
            [quo.react-native :as rn]
            [quo2.foundations.colors :as colors]))

(defn network-breakdown
  []
  [rn/view {:style {:background-color colors/neutral-95
                    :padding 6}}
   [rn/view {:style {:border-bottom-width 1
                     :border-bottom-color colors/neutral-70
                     :padding-vertical 10}}
    [rn/text {:style {:font-size 18
                      :font-weight "600"
                      :color colors/white}}
     "10 ETH"]]
   [rn/scroll-view {:horizontal true
                    :style {:padding-vertical 18
                            :text-align :center}}
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/ethereum40
         {:width 12
          :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50

                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/optimism40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50
                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/arbitrum40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50
                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/zksync40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50
                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/ethereum40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50

                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/optimism40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50
                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/arbitrum40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50
                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]
    [rn/view {:style {:flex-direction :row
                      :padding-horizontal 4}}
     [rn/view
      [rn/view {:flex-direction :row
                :align-items :center}
       [rn/text {:style {:color colors/white}}
        [icons/icon :main-icons/zksync40
         {:width 12
         :color "nil"
          :height 12}] " 5.1234 ETH"]
       [rn/view {:style {:border-right-width 1
                         :margin-left 6
                         :border-right-color colors/neutral-50
                         :height "50%"}}]]
      [rn/text {:style {:margin-left 12
                        :color colors/white}} " on Mainnet"]]]]])