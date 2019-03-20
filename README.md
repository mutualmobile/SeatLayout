Seat Layout
=============
[![Build Status](https://travis-ci.org/mutualmobile/Barricade.svg)](https://travis-ci.org/mutualmobile/SeatLayout.svg?branch=master)

This library is an example implementation of an interactive seat layout which has the following features:

* Pinch to zoom seat arrangement along
* Optional zoom in/out button
* Ability to have multiple seat types like available, sold, reserved, disabled seats etc
* Clickable seats with animated popup showing selected seat information
* Select or deselect multiple seats

<img src="https://github.com/mutualmobile/SeatLayout/blob/master/Artifacts/SeatLayout_showcase.gif">

Download the sample apk from [HERE](https://github.com/mutualmobile/SeatLayout/blob/master/Artifacts/Seatlayout_apk.apk "SeatLayout Apk")

#### How To Use

Since the requirement can be vastly different for different applications, we decided to post an example implementation rather than an injectable library.

This repo has two modules:
* *example*: Which is an example app on how to use SeatLayout. Run this module to test the example app

* *seatlayout*: This is the library that you need to add in your project as a module

SeatLayout has predefined data models and example JSON which you can edit to fit your needs. Following is a piece of brief information to help you do that:


Seat Layout expects an <em>`Array<Array<SeatData>>`</em> so that it can draw seats in the xy plain.

    numberOfRows = seatDataArray.size;
    numberOfColumnsInNthRow = seatDataArray[n].size
    
SeatData Model:
* SeatData is the model that is used to represent each seat in the layout. It contains all the information that is required by SeatLayout for rendering the seat.
* SeatData model contains the `rowIndex, columnIndex, areaIndex, price, description`(Any information that you want to provide when a user taps on the seat)
* SeatStatus: `NONE, EMPTY, SOLD, RESERVED, BROKEN, PLACEHOLDER, UNKNOWN, BUSY, CHOSEN, INFO`
* SeatStyle: `NONE, NORMAL, BARSEAT, HANDICAP, COMPANION, UNKNOWN`
* TableStyle: `NONE, SINGLE, PAIR_LEFT, PAIR_RIGHT, SIDE_TABLE_LEFT, SIDE_TABLE_RIGHT, LONG_LEFT, LONG_CENTER, LONG_RIGHT, LONG_GAP, LONG_GAP_LEFT, LONG_GAP_RIGHT, UNKNOWN`

If you want callbacks, SeatView offers three callbacks right now using SeatListener:
* maxSeatsReached()
* seatTapped()
* updateSelectedSeatDisplay(selectedSeats: Int, maxSelectedSeats: Int)


##### Contributors
* [Shekar](https://github.com/shekaroppo "Shekar")
* [Hitender Pannu](https://github.com/hitenpannu "Hitender Pannu")

License
-------

    Copyright 2019 Mutual Mobile

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

