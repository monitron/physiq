(ns journal
  (:use somnium.congomongo
	helpers
	body
	plan
	food))

(defstruct entry :day :food :exercise :weight :bodyfat :activity)

(defn journal-entry
  "Get the journal entry for a given day"
  [day]
  (let [template {:day day}]
    (apply struct-map entry 
	   (apply concat (or (fetch-one :journal :where template)
			     template)))))

(defn journal-clear!
  "Clear the journal for a given day"
  [day]
  (destroy! :journal {:day day}))

(defn journal-add-food!
  "Add a food eaten to the journal"
  [day meal food]
  (update! :journal {:day day} {:$push {(str "food." meal) food}}))

(defn journal-remove-food!
  "Remove a food eaten from the journal"
  [day meal food]
  (update! :journal {:day day} {:$pop {(str "food." meal) food}}))

(defn journal-record-stats!
  "Record a map of statistics for a day in the journal"
  [day metrics]
  (update! :journal {:day day} {:$set metrics}))


(defn all-food-eaten
  "Given a journal entry, returns a flat list of all food eaten"
  [{food :food}]
  (apply concat (vals food)))

(defn tdee-for-journal-entry
  "Calculates Total Daily Energy Expenditure based on a journal day"
  [{:keys [weight bodyfat activity]}]
  (if (and weight bodyfat)
    (tdee 
     (bmr-katch-mcardle (lean-mass weight bodyfat)) 
     (or activity :sedentary))
    0))

(defn energy-balance-for-journal-entry
  "Calcuates the energy balance for a journal day"
  [entry]
  (- (count-calories (all-food-eaten entry))
     (tdee-for-journal-entry entry)))

(defn variance-for-journal-entry
  "Calculates the difference between the energy balance for a journal day and the applicable plan's calorie adjustment. Negative values mean eat something, positive means exercise"
  [entry]
  (- (energy-balance-for-journal-entry entry)
     (plan-goal-daily-calorie-adjustment (plan-for-journal-entry entry))))

(defn journal-entries-for-date-range
  "Gathers and sorts entries for a date range"
  [start end]
  (sort-by :day
	   (fetch :journal :where {:day {:$gte start :$lte end}})))

(defn simple-stat-over-time
  "Gathers one basic statistic for a date range, returning pairs of date and value"
  [stat start end]
  (let [entries (journal-entries-for-date-range start end)]
    (map (fn [entry] [(:day entry) (stat entry)]) entries)))

(defn computed-stats-over-time
  "Gathers all statistics (including computed ones) for a date range, returning pairs of date and value"
  [start end]
  (let [entries (journal-entries-for-date-range start end)]
    (for [entry entries]
      (let [plan (plan-for-journal-entry entry)]
	[(:day entry)
	 {:weight (:weight entry)
	  :bodyfat (percent-to-human (:bodyfat entry))
	  :lean-mass (lean-mass (:weight entry) (:bodyfat entry))
	  :fat-mass (fat-mass (:weight entry) (:bodyfat entry))
	  :goal-weight (plan-goal-weight-on-date plan (:day entry))}]))))