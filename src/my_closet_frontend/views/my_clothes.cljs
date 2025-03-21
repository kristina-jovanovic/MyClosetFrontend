(ns my-closet-frontend.views.my-clothes
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.events :as events]
            [my-closet-frontend.subs :as subs]
            ))

(defn clothing-card [{:keys [piece-id photo name type color style season]}]
      [:div.clothing-card {:key piece-id}
       [:img {:src photo :alt name}]
       [:div.card-overlay
        [:h3.title {:style {:font-size "20px"}} name]
        [:p (str "Type: " type)]
        [:p (str "Color: " color)]
        [:p (str "Style: " style)]
        [:p (str "Season: " season)]]])

(defn my-clothes-panel []
      (r/create-class
        {:component-did-mount
         (fn []
             (re-frame/dispatch [::events/fetch-clothes])) ;; Dispatch fetch event

         :reagent-render
         (fn []
             (let [clothes @(re-frame/subscribe [::subs/clothes])]
                  [:div.home
                   [:div.liked-container
                    [:h2.title {:style {:color "#cb5b85"}} "Your clothes"]
                    [:div.clothes-grid
                     (if (seq clothes)
                       (map clothing-card clothes)
                       [:p "Loading clothes..."])]]]))}))


(defmethod routes/panels :my-clothes-panel [] [my-clothes-panel])

