package pkgCore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import pkgException.DeckException;
import pkgException.HandException;
import pkgEnum.eBlackJackResult;
import pkgEnum.eGameType;

public class GamePlayBlackJack extends GamePlay {

	private Player pDealer = new Player("Dealer", 0);
	private Hand hDealer = new HandBlackJack();
	
	
	public GamePlayBlackJack(HashMap<UUID, Player> hmTablePlayers, Deck dGameDeck) {
	
		super(eGameType.BLACKJACK, hmTablePlayers, dGameDeck);	
		
		Iterator it = hmTablePlayers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Player p = (Player) pair.getValue();
			Hand h = new HandBlackJack();
			GamePlayerHand GPH = new GamePlayerHand(this.getGameID(), p.getPlayerID(), h.getHandID());
			this.putHandToGame(GPH, h);
		}
	}

	@Override
	protected Card Draw(GamePlayerHand GPH) throws DeckException, HandException {

		Card c = null;

		if (bCanPlayerDraw(GPH)) {
			Hand h = this.gethmGameHand(GPH);
			c = h.Draw(this.getdGameDeck());
			
			h.AddCard(c);
			
			this.putHandToGame(GPH, h);

		}
		return c;
	}

	private boolean bCanPlayerDraw(GamePlayerHand GPH) throws HandException {
		boolean bCanPlayerDraw = false;

		Hand h = this.gethmGameHand(GPH);

		HandScoreBlackJack HSB = (HandScoreBlackJack)h.ScoreHand();
		
		// TODO: Determine if the player can draw another card (are they busted?)
		for(int iScore : HSB.getNumericScores()) {
			if(iScore <= 21) {
				bCanPlayerDraw = true;
				break;
			}
		}
		return bCanPlayerDraw;
	}
	
	
	
	public boolean bDoesDealerHaveToDraw() throws HandException
	{
		boolean bDoesDealerHaveToDraw = true;
		
		HandScoreBlackJack HSB = (HandScoreBlackJack)hDealer.ScoreHand();
		
		//TODO: Determine if the dealer MUST draw.
		for(int iScore : HSB.getNumericScores()) {
			if(iScore >= 17) {
				bDoesDealerHaveToDraw = false;
				break;
			}
		}
		return bDoesDealerHaveToDraw;
	}
	
	
	
	public eBlackJackResult ScoreGame(GamePlayerHand GPH) throws HandException
	{
		HandScoreBlackJack dHSB = (HandScoreBlackJack)hDealer.ScoreHand();
		HandScoreBlackJack pHSB = new HandScoreBlackJack();
		
		Iterator it = this.getHmGameHands().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			GamePlayerHand kGPH = (GamePlayerHand) pair.getKey();
		
			if (kGPH.getGameID() == GPH.getGameID()) {
				HandBlackJack hPlayer = (HandBlackJack) pair.getValue();
				pHSB = (HandScoreBlackJack)hPlayer.ScoreHand();
			}
		}
		
		if (pHSB.getNumericScores().getLast() > dHSB.getNumericScores().getLast()
		& pHSB.getNumericScores().getLast() <= 21) {
			return eBlackJackResult.WIN;
		}
		else if (pHSB.getNumericScores().getLast() < dHSB.getNumericScores().getLast()
		& dHSB.getNumericScores().getLast() <= 21) {
			return eBlackJackResult.LOSE;
		}
		else if (pHSB.getNumericScores().getLast() == dHSB.getNumericScores().getLast()
		& dHSB.getNumericScores().getLast() <= 21 & pHSB.getNumericScores().getLast() <= 21) {
			return eBlackJackResult.TIE;
		}
		else if (pHSB.getNumericScores().getLast() > 21 & dHSB.getNumericScores().getLast() <= 21) {
			return eBlackJackResult.LOSE;
		}
		else {
			return eBlackJackResult.WIN;
		}
	}

	public Player getpDealer() {
		return pDealer;
	}
}
