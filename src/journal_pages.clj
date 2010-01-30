(ns journal-pages
  (:use compojure
	template
	helpers
	date-helpers
	journal
	food
	plan
	body))

(defn journal-section-foods
  [{:keys [day] :as entry}]
  (html
   [:div#foods.section
    [:h2 "Foods"]
    [:ul (for [meal meals :let [foods (get (entry :food) meal)]]
	   [:li.meal (name meal) " (" (count-calories foods) ")"
	    [:ul
	     (for [food foods]
	       [:li.food (food :name) ": " (food :calories)])]])]
    (form-to {:id "addSimpleFood" :class "inactive"}
	     [:post (str "/journal/" (format-ymd day) "/food")]
	     [:h3 "Add Food"]
	     [:ol.forms
	      [:li (label "meal" "Meal:")
	       [:select {:name "meal"}
		(select-options (map name meals) (name (guess-current-meal)))]]
	      [:li (label "name" "Food Name:")
	       (text-field "name")]
	      [:li (label "calories" "Calories:")
	       (text-field "calories")]
	      [:li
	       (submit-button "Add Food") 
	       (link-to {:id "closeAddSimpleFood"} "#" "Cancel")]])
    (link-to { :id "openAddSimpleFood" :class "button" } "#" "Add Food (simple)")
    [:p "Total " (count-calories (all-food-eaten entry)) " calories eaten"]]))


(defn journal-section-stats
  [{:keys [day weight bodyfat activity]}]
  (let [fat (fat-mass weight bodyfat)
	lean (lean-mass weight bodyfat)]
    (html
     [:div#stats.section
      [:h2 "Stats"]
      (form-to {:class "editor"}
	       [:put (str "/journal/" (format-ymd day))]
	       [:ol.forms
		[:li (label "weight" "Weight:")
		 (text-field "weight" (format-float weight))]
		[:li (label "bodyfat" "Body Fat:")
		 (text-field "bodyfat" (format-float (percent-to-human bodyfat)))]
		[:li (label "activity" "Activity Level:")
		 [:select {:name "activity"}
		  (select-options (map name (keys activity-levels)) activity)]]
		[:li 
		 (submit-button "Save") 
		 (link-to {:id "closeEditStats"} "#" "Cancel")]])
      [:div.viewer
       [:ul
	[:li [:strong "Weight: "] 
	 (if (nil? weight) "?" (str (format-float weight) " lbs"))]
	[:li [:strong "Body Fat: "] 
	 (if (nil? bodyfat) "?" (str (format-float (percent-to-human bodyfat)) "%"))]
	[:li [:strong "Activity Level: "]
	 (if (nil? activity) "?" (capitalize activity))]
	[:li [:strong "Fat Mass: "]
	 (if (nil? fat) "?" (str (format-float fat) " lbs"))]
	[:li [:strong "Lean Mass: "]
	 (if (nil? fat) "?" (str (format-float lean) " lbs"))]]
       (link-to {:id "openEditStats"} "#" "Edit")]])))

(defn journal-section-plan
  [{:keys [day] :as entry}]
  (let [plan (plan-for-journal-entry entry)]
    (html
     [:div#plan.section
      [:h2 "Plan"]
      [:img {:src "/charts/weight-over-plan?size=mini"}]
      [:ul
       [:li "Plan day " (+ 1 (plan-day-number-for-date plan day))
	" of " (+ 1 (plan-last-day-number plan))]
       [:li "Ideal weight today: " (format-float (plan-goal-weight-on-date plan day)) " lbs"]
       ]
      ])))

(defn journal-section-guidance
  [entry]
  (let [variance (variance-for-journal-entry entry)
	plan (plan-for-journal-entry entry)]
    (html
     [:div#guidance.section
      [:h2 "Guidance"]
      [:ul
       [:li "Balance: " (energy-balance-for-journal-entry entry)]
       [:li "Desired: " (plan-goal-daily-calorie-adjustment plan)]]
      [:div (cond (< variance 0) (str "You should eat " (- variance) " calories")
		  (> variance 0) (str "Stop eating! You should exercise for " variance " calories")
		  true (str "You nailed it!")) "."]])))

(defn journal-post-food
  [user {:keys [date meal name calories]}]
  (let [date (parse-ymd date)]
    (journal-add-food!
     date
     meal
     (struct-map simple-food :name name :calories (parse-int calories)))
    (redirect-to (str "/journal/" (format-ymd date)))))

(defn journal-put-stats
  [user {:keys [date weight bodyfat activity]}]
  (let [date (parse-ymd date)]
    (journal-record-stats! 
     date
     {
      :weight (parse-float weight)
      :bodyfat (percent-from-human (parse-float bodyfat))
      :activity (keyword activity)
      })
    (redirect-to (str "/journal/" (format-ymd date)))))

(defn journal-home-page
  [user date]
  (let [date (parse-ymd date)
	entry (journal-entry date)]
    (page
     user
     (str "Journal for " 
	  (format-ymd date) 
	  (if (= date (today)) " (Today)"))
     (journal-section-stats entry)
     (journal-section-guidance entry)
     (journal-section-foods entry)
     (journal-section-plan entry))))
