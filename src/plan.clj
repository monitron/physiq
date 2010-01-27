(ns plan
  (:import java.util.Calendar)
  (:use somnium.congomongo
	clojure.contrib.math
	body
	date-helpers))

(defstruct plan :start-date :end-date :start-weight :goal-weight)

(defn plan-for-date
  "Retrieves plan active on a certain date"
  [date]
  (fetch-one :plans)) ; This cheats.

(defn set-plan!
  "Sets plan details"
  [plan]
  (update! :plans {} plan)) ; This is also a cheat.

(defn plan-for-journal-entry
  "Retrieves the plan active for a journal entry"
  [{:keys [day]}]
  (plan-for-date day))

(defn plan-day-number-for-date
  "Given a plan and a date, calculates the day number on which the date falls"
  [{:keys [start-date]} date]
  (days-between date start-date))

(defn plan-last-day-number
  "Given a plan, calculates the day number of the plan end date"
  [plan]
  (plan-day-number-for-date plan (:end-date plan)))

(defn plan-goal-weight-change
  [{:keys [start-weight goal-weight]}] 
  (- goal-weight start-weight))

(defn plan-goal-weight-on-date
  "Given a plan and a date, calculates the ideal weight on that date if the goal is to be reached"
  [{:keys [start-weight goal-weight] :as plan} date]
  (let [day-number (plan-day-number-for-date plan date)
	total-days (plan-last-day-number plan)
	percent-elapsed (/ day-number total-days)]
    (float (+ start-weight (* (plan-goal-weight-change plan) percent-elapsed)))))

(defn plan-goal-daily-weight-change
  "Given a plan, calculates the daily change in weight required (negative for loss)"
  [plan]
  (/ (plan-goal-weight-change plan) (plan-last-day-number plan)))

(defn plan-goal-daily-calorie-adjustment
  "Given a plan, calculates the necessary daily calorie increase or decrease"
  [plan]
  (round (calories-per-pound (plan-goal-daily-weight-change plan))))