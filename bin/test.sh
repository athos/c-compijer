#!/bin/bash

try() {
    expected="$1"
    input="$2"
    clojure -m c-compijer.main "$input" > tmp.s
    gcc -static -o tmp tmp.s
    ./tmp
    actual="$?"
    if [ "$actual" != "$expected" ]; then
        echo "$input expected, but got $actual"
        exit 1
    fi
}

try 0 0
try 42 42

echo OK
