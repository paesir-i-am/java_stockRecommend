package ui;

import recommendation.RecommendationEngine;
import storage.DataStorage;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
  private SearchResultPanel searchResultPanel;
  private RecommendationPanel recommendationPanel;
  private JPanel mainPanel; // CardLayout을 사용할 메인 패널
  private CardLayout cardLayout; // CardLayout 인스턴스
  public SearchResultPanel getSearchResultPanel() {
    return searchResultPanel;
  }
  public RecommendationPanel getRecommendationPanel() {
    return recommendationPanel;
  }

  public MainApp() {
    setTitle("To Mars Stock Recommendation");
    setSize(1500, 1200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // 데이터 저장소와 추천 엔진 초기화
    DataStorage dataStorage = new DataStorage();
    RecommendationEngine recommendationEngine = new RecommendationEngine(dataStorage);

    // 패널 인스턴스 생성
    searchResultPanel = new SearchResultPanel();
    recommendationPanel = new RecommendationPanel(recommendationEngine);

    // CardLayout을 위한 메인 패널
    mainPanel = new JPanel();
    cardLayout = new CardLayout();
    mainPanel.setLayout(cardLayout);

    // 추천 패널과 검색 결과 패널을 CardLayout에 추가
    mainPanel.add(recommendationPanel, "Recommendation");
    mainPanel.add(searchResultPanel, "SearchResults");

    // HeaderPanel을 메인 프레임의 상단에 추가
    HeaderPanel headerPanel = new HeaderPanel(searchResultPanel, this);
    add(headerPanel, BorderLayout.NORTH);
    add(mainPanel, BorderLayout.CENTER); // 메인 패널을 추가

    // 초기화면에 추천 패널 보이기
    cardLayout.show(mainPanel, "Recommendation");
  }

  public void showSearchResults() {
    cardLayout.show(mainPanel, "SearchResults"); // 검색 결과 패널 보여주기
  }

  public static void main(String[] args) {
    // Swing 애플리케이션을 Event Dispatch Thread에서 실행
    SwingUtilities.invokeLater(() -> {
      MainApp app = new MainApp();
      app.setVisible(true);
    });
  }
}
