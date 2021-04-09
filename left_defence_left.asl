//game setup
boundary(-10,50,-12.5,-34).    //x1,x2,y1,y2   field is -52.54 , 52.54, -34, 34
position(-28,-14). //left midd start ::::: pos (10,0) for right
net(52,0). //net position x,y
joining_the_game.


/* Plans */

+joining_the_game <- !join.
+!join : joining_the_game<- -joining_the_game; +before_kick_off; join_team(2,left); !set_up.
+!join <- !set_up.


+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- move(2,-28,-14); -before_kick_off. //; updat_pos(-10,0).

+kick_off_l <- !attack.

+!attack <- dash_to_ball(1);.