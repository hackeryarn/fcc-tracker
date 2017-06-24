# freeCodeCamp Tracker

I built this tool in order to track the progress of FreeCodeCamp. If you are in an organization
that is using FreeCodeCamp as supplementary material, and you want a view into everyone's progress.
This is the perfect tool for you.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

Postgres with postgres user setup.

## Setup

Reference to `init.sql` and use the commends provided to create a database in psql.

Run migrations

    lein run migrate

## Running

To start a web server for the application, run:

    lein run
    
In another terminal window start figwheel for ClojureScript compilation:

    lein figwheel

## License

Copyright © 2017 Artem Chernyak

Distributed uner the MIT License.
