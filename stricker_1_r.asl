// Agent attacker_mid in project krislet_player

/* Initial beliefs and rules */

joining_the_game.


/* Plans */

+joining_the_game <- !join.
+!join : joining_the_game <- -joining_the_game; +before_kick_off; join_team(0,right); !set_up.
+!join <- !set_up.


+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- move(-10,0); -before_kick_off.

+kick_off_l <- !chase_ball.
+kick_off_r <- !chase_ball.

+play_on <- !chase_ball.

+!chase_ball : not(viz("ball", "")) <- turn_to_ball. //!chase_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X > 1.0 & direction("ball", "", Y) & Y > 2.0 <- turn_to_ball. // !chase_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X > 1.0 & direction("ball", "", Y) & Y <= 2.0 <- dash. // !chase_ball.
+!chase_ball : viz("ball", "") & distance("ball", "", X) & X <= 1.0 <- !pass_kick_dribble.
+!chase_ball <- turn_to_ball; !chase_ball.

//agent decides if a player should pass, kick or dribble
//agent decides if a player should pass, kick or dribble
+!pass_kick_dribble : kick_off_r <- kick_start. // !chase_ball.
+!pass_kick_dribble : viz("goal", "l") & distance("goal", "l", X) & X < 25.0  <- kick_to_goal(l). // !chase_ball.
+!pass_kick_dribble : viz("goal", "l") & distance("goal", "l", X) & X >= 25.0 <- dribble. // !chase_ball.
+!pass_kick_dribble : not(viz("goal", "l")) <- !turn_to_g.
//default for +!pass_kick_dribble
+!pass_kick_dribble <- turn_to_ball; !chase_ball.

+!turn_to_g : not(viz("goal", "l")) <- turn_to_goal(l); !turn_to_g.
+!turn_to_g : viz("goal", "l") <- !pass_kick_dribble.
+!turn_to_g <- turn_to_ball; !turn_to_g.

