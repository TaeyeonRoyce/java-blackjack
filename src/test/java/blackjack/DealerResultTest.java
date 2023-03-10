package blackjack;

import static org.assertj.core.api.Assertions.assertThat;

import common.TestDataGenerator;
import domain.blackjack.BlackjackGame;
import domain.card.Card;
import domain.card.Cards;
import domain.blackjack.DealerResult;
import domain.participant.ParticipantName;
import domain.participant.Player;
import domain.participant.Players;
import domain.blackjack.Result;
import domain.card.TrumpCardNumber;
import domain.card.TrumpCardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DealerResultTest {
    private static final Card DIAMOND_TEN = new Card(TrumpCardType.DIAMOND, TrumpCardNumber.TEN);
    private static final Card DIAMOND_NINE = new Card(TrumpCardType.DIAMOND, TrumpCardNumber.NINE);
    private static final Card SPADE_NINE = new Card(TrumpCardType.SPADE, TrumpCardNumber.NINE);
    private static final Card DIAMOND_EIGHT = new Card(TrumpCardType.DIAMOND, TrumpCardNumber.EIGHT);

    private Player pobi;
    private Player crong;
    private Player royce;

    @BeforeEach
    void setUp() {
        pobi = Player.from(new ParticipantName("pobi"));
        crong = Player.from(new ParticipantName("crong"));
        royce = Player.from(new ParticipantName("royce"));
    }

    @DisplayName("블랙잭 게임으로부터 딜러와 플레이어의 경합 결과가 생성된다.")
    @Test
    void createDealerResultSuccessTest() {
        Players players = Players.of(pobi, crong, royce);
        BlackjackGame blackjackGame = TestDataGenerator.getShuffledBlackjackGame(players);
        blackjackGame.getDealer().receive(Cards.of(DIAMOND_TEN, DIAMOND_NINE)); // 19

        pobi.receive(Cards.of(DIAMOND_TEN, SPADE_NINE)); // 19
        crong.receive(Cards.of(DIAMOND_TEN, DIAMOND_TEN)); // 20
        royce.receive(Cards.of(DIAMOND_TEN, DIAMOND_EIGHT)); // 18

        DealerResult dealerResult = DealerResult.from(blackjackGame);

        assertThat(dealerResult.getResultsAgainstParticipants().values())
                .containsExactly(Result.DRAW, Result.LOSE, Result.WIN);
    }
}
