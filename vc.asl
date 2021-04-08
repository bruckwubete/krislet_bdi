// Agent attacker_mid in project krislet_player

/* Initial beliefs and rules */

//game setup
boundary(-10,50,-20,20).    //x1,x2,y1,y2   field is -52.54 , 52.54, -34, 34
position(-10,0). //left midd start ::::: pos (10,0) for right
net(52,0). //net position x,y
before_kick_off.


/* Initial goals */

//!set_up.

/* Plans */

+before_kick_off <- !set_up.

+!set_up <- move(-10,0); -before_kick_off. //; updat_pos(-10,0).

+kick_off_l <- !attack.
+kick_off_r <- !defence.
//+!defence : play_on  <- !defence.

//+ball_in_my_side : play_on <- !defence.  //if the ball is in my zone i defend
//+~ball_in_my_side : play_on <- !attack.    // if the ball is in enemy zone, attack

+!attack : ball_not_in_view <- turn_to_ball.
+!attack : ball_in_view_far <- dash_to_ball.


+!attack : ball_in_view_close <- kick_to_goal; !attack.
//+!defence : play_on <- !attack.
//default for attack
+!attack <- turn_to_ball.

//defence
+!defence <- !wait_for_pass; move(0,0). //middle of the field
//wait for pass
+!wait_for_pass : ball_not_in_view <- turn_to_ball.
+!wait_for_pass : ball_in_view_close <- dribble.
+!wait_for_pass <- turn_to_ball.