# Hoard

A RESTful wrapper to different data stores. Initialy this works for
Mongo (Much in the same way sleepy.mongoose does) but i'd like to
extend this to also work with other data stores. Eventually this 
will also differ by providing aids to applying HATEOAS constraints 
to ensure client and server decoupling.

## Usage

The service can be packaged with lein.

```bash
lein uberjar
```

And the run with 

```bash
java -jar hoard-X.X.jar
```

there is an optional port argument to change from the default 3000

```bash
java -jar hoard-X.X.jar -p 8080
```
 
## License
Copyright © 2013 Christopher Bird

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the
“Software”), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
