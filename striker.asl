// Agent striker in project krislet_player

/* Initial beliefs and rules */
!join_the_game.

+!join_the_game <- join_team(X); !spwan.
+goal_l <- !spwan.
+goal_r <- !spwan.
+goal_kick_r <- !spwan.
+goal_kick_l <- !spwan.

+!spwan <- move(-10, 10);
           !attack.

+!attack : true <- !find_ball;
                       .wait(kick_off_l | play_on);
                       !dash_towards_ball;
                       !find_goal;
                       !strike_to_goal;
                       !attack.

+!find_ball : not(distance("ball", "", X)) <- turn(100); !find_ball.
+!find_ball : direction("ball", "", X) & (X > 30.0 | X < -30.0) <- turn(X); !find_ball.
+!find_ball : true <- .print("done, found ball!").

+!dash_towards_ball
    : (distance("ball", "", X) & X > 1.0) & (direction("ball", "", Y) & (Y >= -2.0 & Y <= 2.0))
        <-  .suspend(find_ball);
            dash(X); !dash_towards_ball.

+!dash_towards_ball : direction("ball", "", X) & (X > 2.0 | X < -2.0) <- turn(X); !dash_towards_ball.
+!dash_towards_ball : true <- .print("done, reached ball!").

+!find_goal : not(distance("goal", "r", X)) <- .suspend(dash_towards_ball); turn(100); !find_goal.
+!find_goal : direction("goal", "r", X) & (X > 10.0 | X < -10.0) <- turn(X); !find_goal.
+!find_goal : true <- .print("done, found goal r").

+!strike_to_goal : distance("goal", "r", X) & direction("goal", "r", Y) <- kick(0.5, Y); .print("done, kicked to goal r").