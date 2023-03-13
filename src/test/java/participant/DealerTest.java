package participant;

import static org.assertj.core.api.Assertions.assertThat;

import common.TestDataGenerator;
import domain.blackjack.BlackjackScore;
import domain.blackjack.Result;
import domain.card.Card;
import domain.card.Cards;
import domain.card.TrumpCardNumber;
import domain.card.TrumpCardType;
import domain.participant.Dealer;
import domain.participant.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DealerTest {
    private static final Card CLUB_ACE = new Card(TrumpCardType.CLUB, TrumpCardNumber.ACE);
    private static final Card HEART_ACE = new Card(TrumpCardType.HEART, TrumpCardNumber.ACE);
    private static final Card HEART_THREE = new Card(TrumpCardType.HEART, TrumpCardNumber.THREE);
    private static final Card HEART_TEN = new Card(TrumpCardType.HEART, TrumpCardNumber.TEN);
    private static final Card CLUB_THREE = new Card(TrumpCardType.CLUB, TrumpCardNumber.THREE);
    private static final Card HEART_QUEEN = new Card(TrumpCardType.HEART, TrumpCardNumber.QUEEN);
    private static final Card SPADE_QUEEN = new Card(TrumpCardType.SPADE, TrumpCardNumber.QUEEN);
    private static final Card SPADE_KING = new Card(TrumpCardType.SPADE, TrumpCardNumber.KING);

    @DisplayName("딜러는 17보다 낮은 경우 카드를 받을 수 있다.")
    @Test
    void isAbleToReceiveCardWhenUnderMoreCardLimitTest() {
        Dealer dealer = TestDataGenerator.getDealer();
        Cards cards = Cards.of(CLUB_ACE, HEART_THREE);
        dealer.start(cards);

        assertThat(dealer.isAbleToReceiveCard()).isTrue();
    }

    @DisplayName("딜러는 17이상인 경우 카드를 받을 수 없다.")
    @Test
    void isAbleToReceiveCardWhenOverMoreCardLimitTest() {
        Dealer dealer = TestDataGenerator.getDealer();
        Cards cards = Cards.of(HEART_QUEEN, HEART_TEN);
        dealer.start(cards);

        assertThat(dealer.isAbleToReceiveCard()).isFalse();
    }

    @DisplayName("딜러는 초기 카드를 한 장만 공개한다.")
    @Test
    void getInitialCardsTest() {
        Dealer dealer = TestDataGenerator.getDealer();
        Cards cards = Cards.of(HEART_QUEEN, HEART_TEN);
        dealer.start(cards);

        Cards initialOpeningCards = dealer.getInitialOpeningCards();
        assertThat(initialOpeningCards.getCards()).hasSize(1);
    }


    @DisplayName("딜러는 플레이어와 경합 한다.")
    @Nested
    class competeWithPlayer {
        Dealer dealer;
        Player player;

        @BeforeEach
        void setUp() {
            dealer = TestDataGenerator.getDealer();
            player = TestDataGenerator.getPlayerWithName("pobi");
        }

        @DisplayName("딜러의 게임 점수가 높으면 승리한다.")
        @Test
        void competeWithPlayerWinTest() {
            dealer.start(Cards.of(HEART_QUEEN, HEART_TEN)); // 20
            player.start(Cards.of(HEART_TEN, HEART_THREE)); // 13

            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.WIN);
        }

        @DisplayName("게임 점수가 낮으면 패배한다.")
        @Test
        void competeWithPlayerWLoseTest() {
            dealer.start(Cards.of(HEART_TEN, HEART_THREE)); // 13
            player.start(Cards.of(HEART_QUEEN, HEART_TEN)); // 20

            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.LOSE);
        }

        @DisplayName("게임 점수가 같으면 비긴다.")
        @Test
        void competeWithPlayerDrawTest() {
            dealer.start(Cards.of(HEART_TEN, HEART_THREE)); // 13
            player.start(Cards.of(HEART_QUEEN, CLUB_THREE)); // 13

            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.DRAW);
        }

        @DisplayName("딜러가 버스트 된 경우 패배한다.")
        @Test
        void competeWithPlayerLoseWhenBustedTest() {
            dealer.start(Cards.of(HEART_TEN, HEART_THREE));
            dealer.receive(HEART_QUEEN);

            player.start(Cards.of(CLUB_THREE, CLUB_ACE));

            assertThat(dealer.isBusted()).isTrue();
            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.LOSE);
        }

        @DisplayName("플레이어가 버스트 된 경우 승리한다.")
        @Test
        void competeWithPlayerWinWhenPlayerBustedTest() {
            dealer.start(Cards.of(CLUB_THREE, CLUB_ACE));
            player.start(Cards.of(HEART_TEN, HEART_THREE));
            player.receive(HEART_QUEEN);

            assertThat(player.isBusted()).isTrue();

            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.WIN);
        }

        @DisplayName("딜러와 플레이어 모두 버스트 된 경우 딜러가 승리한다.")
        @Test
        void competeWithPlayerWinWhenBothBustedTest() {
            dealer.start(Cards.of(CLUB_THREE, SPADE_KING));
            dealer.receive(SPADE_QUEEN);

            player.start(Cards.of(HEART_THREE, HEART_TEN));
            player.receive(HEART_QUEEN);

            assertThat(dealer.isBusted()).isTrue();
            assertThat(player.isBusted()).isTrue();

            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.WIN);
        }

        @DisplayName("점수가 모두 21인 경우 블랙잭을 달성한 참여자가 승리한다.")
        @Test
        void competeWithPlayerWinWhenBlackjackTest() {
            dealer.start(Cards.of(CLUB_ACE, SPADE_KING));

            player.start(Cards.of(HEART_QUEEN, HEART_TEN));
            player.receive(HEART_ACE);

            assertThat(dealer.calculateBlackjackScore()).isEqualTo(BlackjackScore.from(21));
            assertThat(player.calculateBlackjackScore()).isEqualTo(BlackjackScore.from(21));

            Result result = dealer.competeWithPlayer(player);
            assertThat(result).isEqualTo(Result.WIN);
        }
    }

}
