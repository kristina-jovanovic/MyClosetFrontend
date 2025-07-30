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

(re-frame/reg-event-db
  ::filters-success
  (fn [db [_ response]]
      (js/console.log "Filters sent successfully." response)
      db))

(re-frame/reg-event-db
  ::filters-failure
  (fn [db [_ error]]
      (js/console.error "Failure while sending filters." error)
      db))

;get recommendations
(re-frame/reg-event-fx
  ::fetch-recommendations
  (fn [_ _]
      {:http-xhrio {:method          :get
                    :uri             "http://localhost:3000/get-recommendations"
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
                    :params          feedback     ;; Clojure map
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :headers         {"Content-Type" "application/json"}
                    :on-success      [::feedback-saved]
                    :on-failure      [::feedback-error]}}))

(re-frame/reg-event-fx
  ::feedback-saved
  (fn [{:keys [db]} [_ response]]
      {:db (-> db
               (assoc :feedback-message "Feedback saved successfully.")
               (assoc :last-feedback-opinion (:opinion response))) ; pamti opinion (like/dislike) da bi znao da li treba da ga preusmeri
       :dispatch-later [{:ms 3000 :dispatch [::after-feedback-delay]}]}))

(re-frame/reg-event-fx
  ::feedback-error
  (fn [{:keys [db]} [_ error]]
      (let [msg (get-in error [:response :msg] "Error occurred while trying to save feedback.")]
           {:db (assoc db :feedback-message msg)
            :dispatch-later [{:ms 3000 :dispatch [::clear-feedback-message]}]})))

(re-frame/reg-event-fx
  ::after-feedback-delay
  (fn [{:keys [db]} _]
      (let [opinion (:last-feedback-opinion db)]
           (cond
             (= opinion "like")
             {:db (dissoc db :feedback-message :last-feedback-opinion)
              ::navigate "/liked-combinations"}

             (= opinion "dislike")
             {:db (dissoc db :feedback-message :last-feedback-opinion)} ; samo skloni poruku, UI vec prikazuje sledecu kombinaciju

             :else
             {:db (dissoc db :feedback-message :last-feedback-opinion)}))))

(re-frame/reg-event-db
  ::clear-feedback-message
  (fn [db _]
      (assoc db :feedback-message nil)))