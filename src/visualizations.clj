(ns visualizations
  (:use [clojure.contrib.str-utils2 :only (capitalize)]
        (incanter core charts)
	(compojure.http response)
	date-helpers
	helpers
	plan
	journal)
  (:import (java.io ByteArrayOutputStream 
                    ByteArrayInputStream)))

(defn dated-values-to-dataset
  [data columns]
  (let [start-date (first (first data))
	data (map (fn [row]
		    (cons (inc (days-between (first row) start-date))
			  (multi-get (last row) columns))) data)]
    (dataset (cons :day columns) data)))

(defn adjust-plot-axes!
  [plot]
  (let [xyplot (.getXYPlot plot)
	axis (.getRangeAxis xyplot)]
    (.setRangeWithMargins axis (.getDataRange xyplot axis))))

(defn chart-stats
  [start end columns]
  (let [data (dated-values-to-dataset (computed-stats-over-time start end) 
				      columns)
	days (sel data :cols :day)
	primary-column (sel data :cols (first columns))
	secondary-columns (rest columns)
        plot (xy-plot days primary-column 
		      :title ""
		      :x-label ""
		      :y-label ""
		      :series-label (capitalize (name (first columns)))
		      :legend true)]
    (doall (for [col secondary-columns]
	     (add-lines plot days (sel data :cols col)
			:series-label (capitalize (name col)))))
    (adjust-plot-axes! plot)
    plot))

(defn serve-chart
  "Serves a chart PNG directly"
  [request chart]
  (let [out-stream (ByteArrayOutputStream.)
	in-stream (do
		    (save chart out-stream :width 290 :height 240)
		    (ByteArrayInputStream.
		     (.toByteArray out-stream)))
	header {:status 200
		:headers {"Content-Type" "image/png"}}]
    (update-response request header in-stream)))

(defn chart-weight-over-plan
  []
  (let [end (today)
	plan (plan-for-date end)
	start (plan :start-date)]
    (chart-stats start end [:weight :goal-weight])))
