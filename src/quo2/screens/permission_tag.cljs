(ns quo2.screens.permission-tag
  (:require [quo.react-native :as rn]
            [quo.previews.preview :as preview]
            [quo2.components.permission-tag :as permission-tag]
            [quo2.foundations.colors :as colors]
            [status-im.react-native.resources :as resources]
            [reagent.core :as reagent]))

(def descriptor [{:label   "Size:"
                  :key     :size
                  :type    :select
                  :options [{:key   32
                             :value "32"}
                            {:key   24
                             :value "24"}]}
                 {:label "Locked:"
                  :key   :locked
                  :type  :boolean}])

(def community-tokens
  [{:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 1 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}
                                      {:id 5 :token-icon (resources/get-image :status-logo)}
                                      {:id 6 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}
                                      {:id 5 :token-icon (resources/get-image :status-logo)}
                                      {:id 6 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}
                                      {:id 5 :token-icon (resources/get-image :status-logo)}
                                      {:id 6 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}
                                      {:id 5 :token-icon (resources/get-image :status-logo)}
                                      {:id 6 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  3 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}
                     {:id  3 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}
                                      {:id 5 :token-icon (resources/get-image :status-logo)}
                                      {:id 6 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  3 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}]}
   {:token-groups   [{:id  1 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}]}
                     {:id  2 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}]}
                     {:id  3 :tokens [{:id 1 :token-icon (resources/get-image :status-logo)}
                                      {:id 2 :token-icon (resources/get-image :status-logo)}
                                      {:id 3 :token-icon (resources/get-image :status-logo)}
                                      {:id 4 :token-icon (resources/get-image :status-logo)}
                                      {:id 5 :token-icon (resources/get-image :status-logo)}
                                      {:id 6 :token-icon (resources/get-image :status-logo)}]}]}])

(defn cool-preview []
  (let [state  (reagent/atom {:size  24})]
    (fn []
      [rn/view {:margin-bottom 50
                :padding       16}
       [rn/view {:flex 1}
        [preview/customizer state descriptor]]
       [rn/view {:padding-vertical 60
                 :justify-content  :center}
        (when @state
          (for [{:keys [token-groups]} community-tokens]
            ^{:key token-groups}
            [rn/view {:margin-top  20
                      :align-self    :flex-end}
             [permission-tag/tag  (merge @state
                                         {:background-color (colors/theme-colors
                                                             colors/neutral-10
                                                             colors/neutral-80)
                                          :icon              (if (:locked @state) :main-icons2/locked
                                                                 :main-icons2/unlocked)
                                          :icon-color       (colors/theme-colors
                                                             colors/neutral-50
                                                             colors/neutral-40)
                                          :token-groups      token-groups})]]))]])))

(defn preview-permission-tag []
  [rn/view {:background-color (colors/theme-colors
                               colors/white
                               colors/neutral-90)
            :flex              1}
   [rn/flat-list {:flex                      1
                  :keyboardShouldPersistTaps :always
                  :header                    [cool-preview]
                  :key-fn                    str}]])