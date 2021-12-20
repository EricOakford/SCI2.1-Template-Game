;;; Sierra Script 1.0 - (do not remove this comment)
(script# TESTROOM)
(include game.sh) (include "110.shm")
(use Main)
(use LoadMany)
(use Sound)
(use Motion)
(use Game)
(use Actor)
(use System)
(use Print)
(use Polygon)

(public
	rm110 0
)

(instance rm110 of Room
	(properties
		picture 60
		style SHOW_FADE_IN
		horizon 50
		vanishingX 130
		vanishingY 50
		noun N_ROOM
	)
	
	(method (init)
		(super init:)
		(switch prevRoomNum
			; Add room numbers here to set up the ego when coming from different directions.
			(else 
				(ego posn: 300 260)
			)
		)
		(ego init:)
		; We just came from the title screen, so we need to call this to give control
		; to the player.
		(theGame handsOn:)
	)
)