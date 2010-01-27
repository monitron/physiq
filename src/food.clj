(ns food)

(defstruct simple-food :name :calories)

(def meals [:breakfast :lunch :dinner :snacks])

(defn count-calories
  "Returns the total number of calories in a seq of foods"
  [foods]
  (apply + (map #(:calories %) foods)))

(defn guess-current-meal
  "Guesses at a reasonable meal for the current time of day"
  []
  :lunch) ; Make this work