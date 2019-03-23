# freeCodeCamp Tracker DEPRECATED 

**FreecodeCamp moved to using React which prevents the application from scraping the web contents.  However, this project still serves as an example of a full stack Clojure project.**


I built this tool in order to track the progress of FreeCodeCamp. If you are in an organization
that is using FreeCodeCamp as supplementary material, and you want a view into everyone's progress.
This is the perfect tool for you.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

Postgres with postgres user setup.

## Setup

Create database

    lein run init

Run migrations

    lein run migrate

## Running

To start a web server for the application, run:

    lein run
    
In another terminal window start figwheel for ClojureScript compilation:

    lein figwheel
    
## Testing

To run all project tests

    lein test

## License

Copyright © 2017 Artem Chernyak

Distributed uner the MIT License.
