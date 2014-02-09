(ns madot.drawing
  (:require [quil.core :refer :all]))

(def gconfig {:fps 2
              :info-offset 40
              :right-offset 160
              :size 10
              :title "matopeli"
              :background 20})

(defn setup []
  (frame-rate (:fps gconfig))
  (smooth))

(defn stroke-setup
  "Sets all the relevant stroke related attributes."
  [color thickness]
  (stroke color)
  (stroke-weight thickness))

(defn fill-setup
  [color]
  (fill (:r color) (:g color) (:b color)))

(defn draw-circle ([[x y] color]
  (fill-setup color)
  (let [size (:size gconfig)
        off (/ size 2)
        sx (* x size)
        sy (+ (:info-offset gconfig) (* y size))]
    (ellipse (+ sx off) (+ sy off) off off))))

(defn draw-rect
  ([[x y] color]
  (fill-setup color)
  (let [size (:size gconfig)
        sx (* x size)
        sy (+ (:info-offset gconfig) (* y size))]
    (rect sx sy size size))))

(defn draw-grid [gx gy]
  (stroke-setup 40 1)
  (let [height (* (:size gconfig) gy)
        width  (* (:size gconfig) gx)
        offset (:info-offset gconfig)]
    (doseq [i (range 0 gx)]
      (let [x (* (:size gconfig) i)]
        (line x offset x (+ offset height))))
    (doseq [i (range 0 gy)]
      (let [y (+ offset (* (:size gconfig) i))]
        (line 0 y width y)))
    (line width 0 width (+ height offset))))

(defn draw-texts [gx gy ai-list round-number]
  (fill 255)
  (let [width  (* (:size gconfig) gx)
        height (* (:size gconfig) gy)
        offset (:info-offset gconfig)
        roffset (:right-offset gconfig)]
    (text-align :center :bottom)
    (text-size 20)
    (fill 0 255 0)
    (text "MATOMÄHINÄ" (+ width (/ roffset 2)) (+ height offset))
    (text-size 10)
    (fill 255)
    (text-size 12)
    (text-align :left :top)
    (text (str "Vuoro: " round-number) 2 0)
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
  [ai-list]
  (doseq [ai (vals ai-list)
          block @(:blocks ai)]
    (when (true? @(:is-alive ai))
      (draw-rect block (:color ai)))))

(defn- draw-food
  [food]
  (doseq [foo food]
    (draw-circle foo {:r 0 :g 200 :b 0})))

(defn draw-game
  [gx gy ai-list round-number food]
  (background (:background gconfig))
  (draw-grid gx gy)
  (draw-texts gx gy ai-list round-number)
  (draw-ai ai-list)
  (draw-food food))
