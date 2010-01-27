(ns body
  (:use	clojure.contrib.math
	food))

(def activity-levels {:sedentary 1.2, :light 1.375, :moderate 1.55, 
		      :high 1.725, :extreme 1.9})

(defn calories-per-pound
  "Given a number of pounds, calculates the calories required to gain (or lose, for negative values) that amount of weight"
  [lbs]
  (* 3500 lbs))

(defn bmr-katch-mcardle
  "Calculates Basal Metabolic Rate based on lean mass in lbs"
  [lean-mass]
  (+ 370 (* 9.79759519 lean-mass)))

(defn tdee
  "Calculates Total Daily Energy Expenditure based on BMR and activity level"
  [bmr level]
  (round (* bmr (activity-levels (keyword level)))))

(defn lean-mass
  "Calculates lean mass based on weight and body fat percentage"
  [weight bodyfat]
  (if (not-any? nil? [weight bodyfat]) 
    (* weight (- 1 bodyfat))
    nil))

(defn fat-mass
  "Calculates fat mass based on weight and body fat percentage"
  [weight bodyfat]
  (if (not-any? nil? [weight bodyfat]) 
    (* weight bodyfat)
    nil))
