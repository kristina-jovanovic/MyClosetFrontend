(ns my-closet-frontend.views
  (:require
    [re-frame.core :as re-frame]
    [my-closet-frontend.events :as events]
    [my-closet-frontend.routes :as routes]
    [my-closet-frontend.subs :as subs]
    [my-closet-frontend.views.navbar :refer [navbar]]
    [my-closet-frontend.views.footer :refer [footer]]))


;; home

(defn home-panel []
      (let [name (re-frame/subscribe [::subs/name])]
           [:div.home
            [:div.content
             [:h1 "Welcome to MY CLOSET!"]
             ;[:a {:on-click #(re-frame/dispatch [::events/navigate :about])}
             ; "Get an outfit recommendation!"]
             ]]))

(defmethod routes/panels :home-panel [] [home-panel])

;; about

(defn about-panel []
      [:div
       [:h1 "This is the About Page."]

       [:div
        [:a {:on-click #(re-frame/dispatch [::events/navigate :home])}
         "go to Home Page"]]])

(defmethod routes/panels :about-panel [] [about-panel])

;; main

(defn main-panel []
      (let [active-panel (re-frame/subscribe [::subs/active-panel])]
           [:div.main-panel
            [navbar]
            (routes/panels @active-panel)
            [footer]]))
