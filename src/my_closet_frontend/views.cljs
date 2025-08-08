(ns my-closet-frontend.views
  (:require
    [re-frame.core :as re-frame]
    [my-closet-frontend.events :as events]
    [my-closet-frontend.routes :as routes]
    [my-closet-frontend.subs :as subs]
    [my-closet-frontend.views.navbar :refer [navbar]]
    [my-closet-frontend.views.footer :refer [footer]]))

;; home

(defn user-icon []
      [:svg {:width 72 :height 72 :viewBox "0 0 24 24" :fill "currentColor"
             :class "user-icon"}
       [:path {:d "M12 12c2.761 0 5-2.239 5-5s-2.239-5-5-5-5 2.239-5 5 2.239 5 5 5zm0 2c-4.418 0-8 2.239-8 5v1h16v-1c0-2.761-3.582-5-8-5z"}]])

(defn home-panel []
      (let [users-sub (re-frame/subscribe [::subs/users])
            current-id (re-frame/subscribe [::subs/current-user-id])
            current-user (re-frame/subscribe [::subs/current-user])]

           ;; Fetch users on mount
           (re-frame/dispatch [::events/fetch-users])

           (fn []
               (let [users @users-sub
                     cur-id-str (if-let [id @current-id] (str id) "")]

                    [:div.home
                     [:div.content
                      [:h1 {:style {:color "#cb5b85"}} "Welcome to MY CLOSET!"]
                      [:p {:style {:color "white"}} "Combine your clothes into the best combinations with us. Track the combinations you liked and adored."]
                      [:p {:style {:color "white"}} "Quickly peek into your closet no matter where you are."]

                      [:div.user-info-container
                       [user-icon]
                       ;[:div.user-username (or (:username @current-user) "—")]
                       [:div.user-signed-in-label "Signed in as"]
                       ;[:pre (str "Users = " (pr-str @users-sub))]
                       ;[:pre (str "Current ID = " @current-id)]
                       ;[:pre (str "Current user = " (pr-str @current-user))]

                       [:select.user-select
                        {:value     cur-id-str
                         :on-change #(let [v (.. % -target -value)]
                                          (re-frame/dispatch [::events/set-current-user-id v]))}

                        ; placeholder ako user nije izabran
                        (when (empty? cur-id-str)
                              [:option {:value "" :key "user--placeholder"} "Select user..."])

                        (for [{:keys [user-id username]} users
                              :let [id-str (str user-id)]]
                             (when id-str
                                   ^{:key id-str}
                                   [:option {:value id-str} username]))]]]]))))


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
