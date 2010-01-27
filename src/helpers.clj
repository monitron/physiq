(ns helpers
  (:import java.util.Calendar
	   java.util.Date
	   java.text.DecimalFormat))

(defn date-to-day
  "Remove time information from a date"
  [date]
  (let [source-cal (Calendar/getInstance)
        days-only (Calendar/getInstance)]
    (.setTime source-cal date)
    (.clear days-only)
    (doall 
     (for [field [Calendar/YEAR Calendar/MONTH Calendar/DAY_OF_MONTH]]
       (.set days-only field (.get source-cal field))))
    (.getTime days-only)))

(defn day
  "Make a date (no time) from a year, month and day. Java months start with 0"
  [year month day]
  (.getTime 
   (doto (Calendar/getInstance)
     (.clear)
     (.set Calendar/YEAR year)
     (.set Calendar/MONTH month)
     (.set Calendar/DAY_OF_MONTH day))))

(defn today
  "Make a date (no time) for the current date"
  []
  (date-to-day (Date.)))

(defn percent-to-human
  "Turn 0.352 into 35.2"
  [pct]
  (if (nil? pct)
    nil
    (* pct 100)))

(defn percent-from-human
  "Turn 35.2 into 0.352"
  [pct]
  (if (nil? pct)
    nil
    (/ pct 100)))

(defn parse-float
  [string]
  "Parse a float, catching exceptions and returning nil"
  (try (Float/parseFloat (str string)) (catch java.lang.Exception _ nil)))

(defn parse-int
  [string]
  "Parse an int, catching exceptions and returning nil"
  (try (Integer/parseInt (str string)) (catch java.lang.Exception _ nil)))

(defn format-float
  [float]
  "Format a float as xxx.x for human consumption"
  (try (.format (DecimalFormat. "#0.0") float) (catch java.lang.IllegalArgumentException _ nil)))

(defn multi-get
  [m keys]
  "Retrieve multiple keys from a map and return their values"
  (map (fn [k] (k m)) keys))