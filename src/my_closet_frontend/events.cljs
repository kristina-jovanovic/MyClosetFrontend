(ns my-closet-frontend.events
  (:require
    [re-frame.core :as re-frame]
    [my-closet-frontend.db :as db]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [ajax.core :as ajax]
    [ajax.edn :refer [edn-response-format]]
    [cljs.tools.reader :as reader]
    ;[cljs.reader :as reader]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [clojure.string :as str]))

; helper: sigurno pretvori u broj ili vrati nil
(defn ->num [x]
      (let [n (js/Number x)]
           (when (not (js/isNaN n)) n)))

(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
             db/default-db))

(re-frame/reg-event-fx
  ::navigate
  (fn-traced [_ [_ handler]]
             {:navigate handler}))

(re-frame/reg-event-fx
  ::set-active-panel
  (fn-traced [{:keys [db]} [_ active-panel]]
             {:db (assoc db :active-panel active-panel)}))

;my-clothes

(re-frame/reg-event-fx
  ::fetch-clothes
  (fn [_ _]
      {:http-xhrio {:method          :get
                    :uri             "http://localhost:3000/my-clothes"
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-clothes-success]
                    :on-failure      [::fetch-clothes-failure]}}))

(re-frame/reg-event-db
  ::fetch-clothes-success
  (fn [db [_ response]]
      (js/console.log "Fetched Clothes:" response)
      (assoc db :clothes response)))

(re-frame/reg-event-db
  ::fetch-clothes-failure
  (fn [db [_ error]]
      (js/console.error "Failed to fetch clothes:" error)
      db))

;set filters - send it to back
(re-frame/reg-event-fx
  ::send-filters
  (fn [_ [_ filters]]
      {:http-xhrio {:method          :post
                    :uri             "http://localhost:3000/get-recommendations"
                    :params          filters
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :headers         {"Content-Type" "application/json"}
                    :on-success      [::filters-success]
                    :on-failure      [::filters-failure]}}))

(re-frame/reg-event-fx
  ::filters-success
  (fn [_ [_ response]]
      (js/console.log "Filters sent successfully." (clj->js response))
      ;(set! (.-location js/window) "/recommendations")      ; VIDI OVO
      {:navigate [:recommendations]}))


(re-frame/reg-event-db
  ::filters-failure
  (fn [db [_ error]]
      (js/console.error "Failure while sending filters." error)
      db))

;get recommendations
(re-frame/reg-event-fx
  ::fetch-recommendations
  (fn [_ [_ user-id]]
      {:http-xhrio {:method          :get
                    :uri             (str "http://localhost:3000/get-recommendations?user-id=" (->num user-id))
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-recommendations-success]
                    :on-failure      [::fetch-recommendations-failure]}}))

(re-frame/reg-event-db
  ::fetch-recommendations-success
  (fn [db [_ response]]
      (js/console.log "Fetched Recommendations:" (clj->js response))
      (assoc db :combinations (mapv vec response))))

(re-frame/reg-event-db
  ::fetch-recommendations-failure
  (fn [db [_ error]]
      (js/console.error "Failed to fetch recommendations:" error)
      db))

(re-frame/reg-event-fx
  ::insert-feedback
  (fn [_ [_ feedback]]
      {:http-xhrio {:method          :post
                    :uri             "http://localhost:3000/insert-feedback"
                    :params          feedback               ;; Clojure map
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :headers         {"Content-Type" "application/json"}
                    :on-success      [::feedback-saved]
                    :on-failure      [::feedback-error]}}))

(re-frame/reg-event-fx
  ::after-feedback-delay
  (fn [{:keys [db]} _]
      (let [opinion (:last-feedback-opinion db)]
           (cond
             (or (= opinion "like") (= opinion :like))
             {:db         (dissoc db :feedback-message :last-feedback-opinion)
              :navigate   [:liked-combinations]
              :dispatch-n [[::fetch-liked-combinations (:current-user-id db)]]}

             :else
             {:db (dissoc db :feedback-message :last-feedback-opinion)} ; samo skloni poruku, UI vec prikazuje sledecu kombinaciju
             ))))

(re-frame/reg-event-fx
  ::feedback-saved
  (fn [{:keys [db]} [_ response]]
      {:db             (-> db
                           (assoc :feedback-message "Feedback saved successfully.")
                           (assoc :last-feedback-opinion (name (:opinion response)))) ;PROBAJ OVO
       ;(assoc :last-feedback-opinion (:opinion response))) ; pamti opinion (like/dislike) da bi znao da li treba da ga preusmeri
       :dispatch-later [{:ms 3000 :dispatch [::after-feedback-delay]}]}))

(re-frame/reg-event-fx
  ::feedback-error
  (fn [{:keys [db]} [_ error]]
      (let [msg (get-in error [:response :msg] "Error occurred while trying to save feedback.")]
           {:db             (assoc db :feedback-message msg)
            :dispatch-later [{:ms 3000 :dispatch [::clear-feedback-message]}]})))

(re-frame/reg-event-db
  ::clear-feedback-message
  (fn [db _]
      (assoc db :feedback-message nil)))

;get liked-combinations
(re-frame/reg-event-fx
  ::fetch-liked-combinations
  (fn [_ [_ user-id]]                                       ; event vektor je npr [::fetch-liked-combinations 1]
      {:http-xhrio {:method          :get
                    :uri             (str "http://localhost:3000/liked-combinations?user-id=" (->num user-id))
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-liked-combinations-success]
                    :on-failure      [::fetch-liked-combinations-failure]}}))

(re-frame/reg-event-db
  ::fetch-liked-combinations-success
  (fn [db [_ response]]
      (let [parsed (map #(into {} %) response)]
           (js/console.log "Fetched liked combinations:" (clj->js response))
           (assoc db :liked-combinations parsed))))


(re-frame/reg-event-db
  ::fetch-liked-combinations-failure
  (fn [db [_ error]]
      (js/console.error "Failed to fetch liked combinations:" error)
      db))

;update/set rating
(re-frame/reg-event-fx
  ::update-rating
  (fn [{:keys [db]} [_ combination-id new-rating]]
      (let [user-id (:current-user-id db)]
           {:http-xhrio {:method          :put
                         :uri             "http://localhost:3000/update-rating"
                         :params          {:combination-id combination-id
                                           :rating         new-rating
                                           :user-id        (->num user-id)}
                         :format          (ajax/json-request-format)
                         :response-format (ajax/json-response-format {:keywords? true})
                         :on-success      [::update-rating-success combination-id new-rating]
                         :on-failure      [::update-rating-failure]}})))

(re-frame/reg-event-fx
  ::update-rating-success
  (fn [{:keys [db]} [_ combination-id new-rating response]]
      (let [user-id (:current-user-id db)]
           (js/console.log "Rating updated successfully:" (clj->js response))
           {:db         (update db :liked-combinations
                                (fn [items]
                                    (->> (or items [])
                                         (mapv (fn [comb]
                                                   (if (= (:combination-id comb) combination-id)
                                                     (assoc comb :rating new-rating)
                                                     comb))))))
            :dispatch-n [[::fetch-favorite-combinations user-id]]})))


(re-frame/reg-event-fx
  ::update-rating-failure
  (fn [_ [_ error]]
      (js/console.error "Failed to update rating:" (clj->js error))
      {}))

;get favorite combinations
(re-frame/reg-event-fx
  ::fetch-favorite-combinations
  (fn [_ [_ user-id]]
      (js/console.log "FETCHING favorites for user-id:" (->num user-id))
      {:http-xhrio {:method          :get
                    :uri             (str "http://localhost:3000/favorite-combinations?user-id=" (->num user-id))
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-favorite-combinations-success]
                    :on-failure      [::fetch-favorite-combinations-failure]}}))

(re-frame/reg-event-db
  ::fetch-favorite-combinations-success
  (fn [db [_ response]]
      (let [parsed (map #(into {} %) response)]
           (js/console.log "Fetched favorite combinations:" (clj->js response))
           (assoc db :favorite-combinations parsed))))


(re-frame/reg-event-db
  ::fetch-favorite-combinations-failure
  (fn [db [_ error]]
      (js/console.error "Failed to fetch favorite combinations:" error)
      db))


;get users
(re-frame/reg-event-fx
  ::fetch-users
  (fn [_ _]
      {:http-xhrio {:method          :get
                    :uri             "http://localhost:3000/users"
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-users-success]
                    :on-failure      [::fetch-users-failure]}}))

(re-frame/reg-event-db
  ::fetch-users-success
  (fn [db [_ users]]
      (let [users' (->> users
                        (mapv #(update % :user-id js/parseInt 10))
                        (remove nil?))
            db' (assoc db :users users')]
           (if (number? (:current-user-id db'))
             db'                                            ; vec postoji, ne menjaj
             (assoc db' :current-user-id (:user-id (first users')))))))


(re-frame/reg-event-db
  ::fetch-users-failure
  (fn [db [_ err]]
      (js/console.error "Failed to fetch users:" (clj->js err))
      db))

; kad se promeni user u dropdownu, azuriramo db i refetchujemo favorites i recommendations
(re-frame/reg-event-fx
  ::set-current-user-id
  (fn [{:keys [db]} [_ id-str]]
      (let [user-id (js/parseInt id-str 10)]
           (js/console.log "Current " id-str)
           (if (js/isNaN user-id)
             {:db db}
             {:db         (assoc db :current-user-id user-id)
              :dispatch-n [[::fetch-favorite-combinations user-id]
                           ;[::fetch-recommendations user-id]
                           [::fetch-liked-combinations user-id]]
              }))))

