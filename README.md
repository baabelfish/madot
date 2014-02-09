# madot

Multiplayer AI-challenge on top of clojure and snakes.

![Wizard vs Wallhugger](https://raw.github.com/baabelfish/madot/master/doc/madot.png)

## Usage

### Running
```
$ lein run
```

### Creating an AI
1. Make an AI into file `src/ai/<NAME>.clj`
2. Add the AI to `src/ai/index.clj`
   - Merge it to `:require` like so: `(:require [ai.wallhugger :as wallhugger] [ai.<NAME> :as <NAME>])`
   - Add it to the array that ai-index function returns as `ai.<NAME>/init` e.g. `[ai.wallhugger/init ai.<NAME>/init]`
3. Run it

### Rules
You are only allowed to use the data `exec`-function is provided with and the
`madot.helpers`-namespace.

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
