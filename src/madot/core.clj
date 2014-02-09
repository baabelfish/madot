(ns madot.core
  (:require [quil.core :refer :all]
            [ai.index :as index]
            [madot.drawing :refer :all]))

(def config {:x 40
             :y 40
             :food-density 10})

(def options #{:left
               :right
               :up
               :down})

(def directions {:left [-1 0]
                 :right [1 0]
                 :up [0 -1]
                 :down [0 1]})

(defn in?
  [se elem]
  (some #(= elem %) se))

(defn vecoper
  [[x y] [dx dy] f]
  [(f dx x) (f dy y)])

(defn- between?
  "Checks if a value is between a and b"
  [value a b]
  (and (>= value a) (< value b)))

(defn- random-tile
  []
  [(int (rand (dec (:x config))))
   (int (rand (dec (:y config))))])

(defn- on-grid?
  "Checks if the given position is on grid."
  [[x y]]
  (let [sizex (:x config)
        sizey (:y config)]
    (and (between? x 0 sizex) (between? y 0 sizey))))

(def ai-list (zipmap
               (map inc (range))
               (->> (index/ai-index)
                    (map #(merge % {:blocks (atom (conj '() (random-tile)))
                                    :size (atom 2)
                                    :is-alive (atom true)})))))

(def round-number (atom 0))
(def nonpassable (atom []))
(def food (atom #{}))

(defn- kill-ai
  ([ai]
   (swap! (:is-alive ai) (fn [x] false)))
  ([ai1 ai2]
   (kill-ai ai1)
   (kill-ai ai2)))

(defn- check-collisions []
  (doseq [ai (vals ai-list)
          aic (vals ai-list)]
    (when (and (true? @(:is-alive ai)) (true? @(:is-alive ai)))
      (let [aib @(:blocks ai)
            aicb @(:blocks aic)]
        (if (and (not= ai aic) (= (first aib) (first aicb)))
          (kill-ai ai aic)
          (when (or (in? (rest aicb) (first aib))
                    (not (on-grid? (first aib))))
            (kill-ai ai)))))))

(defn- pickup-food []
  (doseq [ai (vals ai-list)]
    (let [head (first @(:blocks ai))]
      (when (contains? @food head)
        (swap! (:size ai) inc)
        (swap! food #(disj % head))))))

(defn- run-ai []
  (swap! round-number inc)
  (when (zero? (mod @round-number (:food-density config)))
    (swap! food #(conj % (random-tile))))
  (pickup-food)
  (doseq [ai (vals ai-list)]
    (when (true? @(:is-alive ai))
      (let [move ((:exec ai))]
        (if (contains? options move)
          (let [blocks (:blocks ai)
                size @(:size ai)
                newpos (vecoper (first @blocks) (move directions) +)]
            (swap! blocks #(conj % newpos))
            (when (> (count @blocks) size)
              (swap! blocks butlast)))
          (kill-ai ai)))))
  (check-collisions))

(defn- game []
  (draw-game (:x config) (:y config) ai-list @round-number @food)
  (run-ai))

(defn -main []
  (let [sizex (* (:size gconfig) (:x config))
        sizey (* (:size gconfig) (:y config))]
    (sketch
      :title (:title gconfig)
      :setup setup
      :draw game
      :size [(+ sizex (:right-offset gconfig)) (+ sizey (:info-offset gconfig))]))
  (println "Matomähinä käynnis!"))
