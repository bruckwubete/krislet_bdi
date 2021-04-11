// Agent attacker_mid in project krislet_player

/* Initial beliefs and rules */
joining_the_game.


/* Plans */

+joining_the_game <- !join.
+!join : joining_the_game <- -joining_the_game; +before_kick_off; join_team(5,right); !set_up.
+!join <- !set_up.


+goal_l <- !set_up.
+goal_r <- !set_up.

+!set_up <- move(10.5,0); -before_kick_off. //; updat_pos(-10,0).

+kick_off_r <- !attack.
+kick_off_l <- !defence.

//+ball_in_my_side : play_on <- !defence.  //if the ball is in my zone i defend
//+~ball_in_my_side : play_on <- !attack.    // if the ball is in enemy zone, attack

+play_on <- !attack.


+!attack : not(viz("ball", "")) <- turn_to_ball. //!attack.
+!attack : viz("ball", "") & distance("ball", "", X) & X > 1.0 & direction("ball", "", Y) & Y > 2.0 <- turn_to_ball. //!attack.
+!attack : viz("ball", "") & distance("ball", "", X) & X > 1.0 & direction("ball", "", Y) & Y <= 2.0 <- dash. //!attack.
+!attack : viz("ball", "") & distance("ball", "", X) & X <= 1.0 <- !pass_kick_dribble. //!attack.

//default for attack
+!attack <- turn_to_ball; !attack.

//agent decides if a player should pass, kick or dribble
+!pass_kick_dribble : kick_off_r <- -kick_off_r; kick_start.
+!pass_kick_dribble : viz("goal", "l") & distance("goal", "l", X) & X < 25.0  <- kick_to_goal.
+!pass_kick_dribble : viz("goal", "l") & distance("goal", "l", X) & X >= 25.0 <- dribble.
+!pass_kick_dribble : not(viz("goal", "l")) <- !turn_to_g.
//default for +!pass_kick_dribble
+!pass_kick_dribble <- turn_to_ball; !attack.

+!turn_to_g : not(viz("goal", "l")) <- turn_to_goal; !turn_to_g.
+!turn_to_g : viz("goal", "l") <- !pass_kick_dribble.
+!turn_to_g <- turn_to_ball; !turn_to_g.

+!defence <- !attack. //temp 
