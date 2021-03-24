# auto-rng-timer
automatic-starting timer for pokemon rng

# this code is a mess
and it will probably stay that way.

# usage
place jar file in a folder with any .wav sound file named "beep.wav"
if the sound file doesnt exist it wont work properly. you need a sound file.

open a window that displays a view of your system capture (obs will work fine (gamecube) - or ds cap card software - or emulator)
run the jar, itll play the sound file once and a window will pop up with a screenshot of your primary display. If your game view is not on the primary display, you can use the number keys on your keyboard to switch between displays. If the game view was not visible when the timer was opened, then you can simply hit the key corresponding to the monitor with the preview to re-take the screenshot. If the window covers the game, you can resize it to be nice and small when hitting the number keys to take the screenshot.

Using leftclick (controls top left selection corner) and rightclick (controls bottom right selection corner), select a decent amount of the game view window, it doesnt need to be perfect, but make sure ONLY the game is in the selection. If the game view is moved or resized you will have to restart the timer.

Once satisfied with your selection, press the "Enter" key on your keyboard. The selection window will close and a timer window will open.

In the window you can switch between milliseconds mode, frames mode, choose the fps for frames mode, and switch the "trigger" mode for when the timer starts.
You can have multiple "chained" timers by splitting the milliseconds/frames (depending on selected mode) with a ,
ex: 7000,13000 will make a 7 second timer, followed by a 13 second timer. IT DOES NOT MAKE A 13 SECOND TIMER THAT BEEPS AT BOTH 7 AND 13 SECONDS! To do that you would just do 7000,6000, as that totals 13 seconds.

You may have to adjust timings from other timers slightly based on your capture cards latency. Using a DS capture card the latency is minimal and I didn't have to adjust for it at all, however a gamecube with elgato may have to have the times adjusted. (subtract the capture cards latency from the time)

When you press start, the timer will begin waiting for a reset to occur, it detects resets by checking for a solid color frame in the game view, it checks at the same rate as selected in the fps dropdown.

# the "trigger" modes

first mode will start the timer on the very first frame that is a solid color. This is essentially the same as resetting the game when the pre-timer in eontimer reaches 0, or pressing a key to start flowtimer and resetting the game at the same time. This mode only removes human error in the time between resetting and starting the timer, and does not remove the desync caused by the amount of time that the reset combination was held. This mode is only slightly more accurate than just using flowtimer or eontimer.

last mode starts the timer on the first non solid color frame that appears after a solid color frame. This will require some adjustments on your times as the solid color can last for multiple frames upon reset, depending on the game. Once adjusted however, this mode becomes extremely accurate. You can hold the reset combo for as long as you want, and this will always sync with the exact same frame, therefore being in perfect sync with the game.
