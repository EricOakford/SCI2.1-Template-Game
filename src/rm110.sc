;;; Sierra Script 1.0 - (do not remove this comment)
(script# 110)
(include game.sh) (include 110.shm)
(use Actor)
(use Door)
(use Feature)
(use Game)
(use Main)
(use Polygon)
(use System)

(public
	rm110 0
)

(instance rm110 of Room
	(properties
		picture scriptNumber
		noun N_ROOM
	)
	
	(method (init)
		(super init:)
		(switch prevRoomNum
			(else 
				(ego posn: 300 200)
			)
		)
		(ego init:)
	)
)