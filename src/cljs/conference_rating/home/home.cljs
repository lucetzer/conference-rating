(ns conference-rating.home
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :as forms]
            [ajax.core :as ajax]
            [conference-rating.history :as history]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [conference-rating.panel :as panel]
            [conference-rating.add-rating :as add-rating]
            [conference-rating.form :as form]
            [conference-rating.rating :as rating]
            [conference-rating.conference :as conference]
            [conference-rating.conference.add-conference :as add-conference]
            [conference-rating.conference.conference-list :as conference-list]))

(defonce displayed-conference (atom nil))
(defonce conferences (atom nil))
(defonce ratings (atom nil))

;; -------------------------
;; Requests
(defn load-conference [id]
  (ajax/GET (str "/api/conferences/" id) {:handler #(reset! displayed-conference %1)
                                          :error-handler #(js/alert (str "conference not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conference-ratings [id]
  (ajax/GET (str "/api/conferences/" id "/ratings") {:handler #(reset! ratings %1)
                                          :error-handler #(js/alert (str "ratings not found" %1))
                                          :response-format :json
                                          :keywords? true}))

(defn load-conferences []
  (ajax/GET "/api/conferences" {:handler #(reset! conferences %1)
                                :error-handler #(js/alert (str "conferences not found" %1))
                                :response-format :json
                                :keywords? true}))


;; -------------------------
;; Views

(defn display-loading []
  [:div [:h2 "Loading..."]])

(defn display-ratings [conference-ratings]
  [:div (map rating/display-rating conference-ratings)])

(defn display-conference [conference]
  (let [conference-ratings @ratings]
  [:div {:class "container"}
   (conference/display-conference-overview conference)
   (add-rating/add-rating (:_id conference))
   [:div (display-ratings conference-ratings)]]))

(defn conference-page []
  (let [conference @displayed-conference]
    (if (not (nil? conference))
      (display-conference conference)
      (display-loading))))

(defn conferences-page []
  (let [conference-list @conferences]
    (if (not (nil? conference-list))
      (conference-list/display-conference-list conference-list)
      (display-loading))))