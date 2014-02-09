(ns madot.helpers)

(defn in?
  ;; FIXME
  "Checks if an element is in a sequence."
  [se elem]
  (some #(= elem %) se))

(defn random-point
  "Gives a random point on field."
  [x y]
  [(int (rand (dec x)))
   (int (rand (dec y)))])

(defn between?
  "Checks if a value is between a and b"
  [value a b]
  (and (>= value a) (< value b)))

(defn vecoper
  [[x y] [dx dy] f]
  [(f dx x) (f dy y)])

(defn on-grid?
  "Checks if the given position is on the grid."
  [[x y] [gx gy]]
  (and (between? x 0 gx) (between? y 0 gy)))
