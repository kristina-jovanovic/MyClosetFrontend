(ns my-closet-frontend.views.get-recommendation
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.events :as events]))

(defonce selections (r/atom {:casual true
                             :work false
                             :formal false
                             :party false
                             :summer true
                             :winter false}))

(defn toggle-selection [key]
      (swap! selections
             (fn [s]
                 (cond

                   (= key :winter) (-> s
                                       (assoc :winter (not (:winter s)))
                                       (assoc :summer false))

                   (= key :summer) (-> s
                                       (assoc :summer (not (:summer s)))
                                       (assoc :winter false))

                   (= key :work) (-> s
                                       (assoc :work (not (:work s)))
                                       (assoc :casual false)
                                       (assoc :formal false)
                                       (assoc :party false))

                   (= key :formal) (-> s
                                     (assoc :formal (not (:formal s)))
                                     (assoc :casual false)
                                     (assoc :work false)
                                     (assoc :party false))

                   (= key :party) (-> s
                                     (assoc :party (not (:party s)))
                                     (assoc :casual false)
                                     (assoc :formal false)
                                     (assoc :work false))

                   (= key :casual) (-> s
                                     (assoc :casual (not (:casual s)))
                                     (assoc :work false)
                                     (assoc :formal false)
                                     (assoc :party false))

                   :else (update s key not)))))


(defn get-recommendation-panel []
      [:div.home
       [:div.get-recommendation-panel.container
       [:p.get-recommendation-title "Get your outfit recommendation"]
        [:p "Please mark if you have any specific requests"]

       [:div.switch-container
        [:span "Casual"]
        [:label.switch
         [:input {:type "checkbox"
                  :checked (:casual @selections)
                  :on-change #(toggle-selection :casual)}]
         [:span.slider]]]

        [:div.switch-container
         [:span "Work"]
         [:label.switch
          [:input {:type "checkbox"
                   :checked (:work @selections)
                   :on-change #(toggle-selection :work)}]
          [:span.slider]]]

       [:div.switch-container
        [:span "Formal"]
        [:label.switch
         [:input {:type "checkbox"
                  :checked (:formal @selections)
                  :on-change #(toggle-selection :formal)}]
         [:span.slider]]]

        [:div.switch-container
         [:span "Party"]
         [:label.switch
          [:input {:type "checkbox"
                   :checked (:party @selections)
                   :on-change #(toggle-selection :party)}]
          [:span.slider]]]

        [:br]

       [:div.switch-container
        [:span "Summer"]
        [:label.switch
         [:input {:type "checkbox"
                  :checked (:summer @selections)
                  :on-change #(toggle-selection :summer)}]
         [:span.slider]]]

       [:div.switch-container
        [:span "Winter"]
        [:label.switch
         [:input {:type "checkbox"
                  :checked (:winter @selections)
                  :on-change #(toggle-selection :winter)}]
         [:span.slider]]]

       [:button.button
        {:on-click #(re-frame/dispatch [::events/navigate :recommendations])}
        "Get recommendation"]]])

(defmethod routes/panels :get-recommendation-panel [] [get-recommendation-panel])
