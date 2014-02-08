(ns madot.core
  (:require [quil.core :refer :all]
            [ai.index :as index]))

(def config {:x 40
             :y 40
             :food-density 10
             :fps 2
             :info-offset 40
             :right-offset 160
             :size 10
             :title "matopeli"
             :background 20})

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

(defn- setup []
  (frame-rate (:fps config))
  (smooth))

(defn- stroke-setup
  "Sets all the relevant stroke related attributes."
  [color thickness]
  (stroke color)
  (stroke-weight thickness))

(defn- fill-setup
  [color]
  (fill (:r color) (:g color) (:b color)))

(defn- dgrid []
  (stroke-setup 40 1)
  (let [height (* (:size config) (:y config))
        width  (* (:size config) (:x config))
        offset (:info-offset config)]
    (doseq [i (range 0 (:x config))]
      (let [x (* (:size config) i)]
        (line x offset x (+ offset height))))
    (doseq [i (range 0 (:y config))]
      (let [y (+ offset (* (:size config) i))]
        (line 0 y width y)))
    (line width 0 width (+ height offset))))

(defn- dcircle
  ([[x y] color]
  (fill-setup color)
  (let [size (:size config)
        off (/ size 2)
        sx (* x size)
        sy (+ (:info-offset config) (* y size))]
    (ellipse (+ sx off) (+ sy off) off off))))

(defn- drect
  ([[x y] color]
  (fill-setup color)
  (let [size (:size config)
        sx (* x size)
        sy (+ (:info-offset config) (* y size))]
    (rect sx sy size size))))

(defn- draw-text []
  (fill 255)
  (let [width  (* (:size config) (:x config))
        height (* (:size config) (:y config))
        offset (:info-offset config)
        roffset (:right-offset config)]
    (text-align :center :bottom)
    (text-size 20)
    (fill 0 255 0)
    (text "MATOMÄHINÄ" (+ width (/ roffset 2)) (+ height offset))
    (text-size 10)
    (fill 255)
    (text-size 12)
    (text-align :left :top)
    (text (str "Vuoro: " @round-number) 2 0)
    (loop [index 1]
      (when (< index (inc (count ai-list)))
        (let [ai (get ai-list index)
              color (:color ai)]
          (when (true? @(:is-alive ai))
            (fill-setup color)
            (rect (+ 2 width) (+ 2 (* 12 (dec index))) 8 8))
          (fill 255)
          (text (str (:name ai) " (" index ")") (+ width 12) (* 12 (dec index)))
          (recur (inc index)))))))

(defn- draw-ai
  []
  (doseq [ai (vals ai-list)
          block @(:blocks ai)]
    (when (true? @(:is-alive ai))
      (drect block (:color ai)))))

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

(defn- draw-food []
  (doseq [foo @food]
    (dcircle foo {:r 0 :g 200 :b 0})))

(defn- game []
  (background (:background config))
  (dgrid)
  (draw-text)
  (draw-food)
  (draw-ai)
  (run-ai))

(defn -main []
  (let [sizex (* (:size config) (:x config))
        sizey (* (:size config) (:y config))]
    (sketch
      :title (:title config)
      :setup setup
      :draw game
      :size [(+ sizex (:right-offset config)) (+ sizey (:info-offset config))]))
  (println "Matomähinä käynnis!"))
