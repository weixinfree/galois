## Galois Lang

`Version 0.0.1`

### Features

- small, simple
- base on Java (Yes Java, Not JVM)
- 解释型语言
- syntax like lisp, S expression
- eager evaluated
- block scope
- support closure
- high order function
- function is first class


### Hello World
```go
(println 'hello world')
```

### Code Style Taste
```go
(do
     (record User (name age sex weight))
     (let xm (User ('xm' 10 'male' 62.8)))

     (fn join (li sep)
         (do
             (let sb (new StringBuilder))
             (let last (int (- (len li) 1)))
             (iter li
                 (do
                     (. sb :append (str $it))
                     (if (!= $index last)
                         (. sb :append sep)
                         (None))))
             (let result (str sb))))

     (println (join (list 1 2 3) ','))

     (fn format-user (user)
         (do
             (str 'User{' (join (values xm) ', ') '}')))

     (println (format-user xm))
)
```