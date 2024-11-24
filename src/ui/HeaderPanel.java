package ui;

import model.StockInfo;
import storage.DataStorage;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class HeaderPanel extends JPanel {
  private JTextField searchField;
  private JPopupMenu suggestionsPopup;
  private DataStorage dataStorage;
  private int currentIndex = -1;
  private boolean isHighlighting = false;
  private SearchResultPanel searchResultPanel;
  private ui.MainApp mainApp; // MainApp 인스턴스 추가

  public HeaderPanel(SearchResultPanel searchResultPanel, ui.MainApp mainApp) {
    this.searchResultPanel = searchResultPanel;
    this.mainApp = mainApp; // MainApp 인스턴스 할당
    dataStorage = new DataStorage(); // 데이터 저장소 초기화
    setLayout(new BorderLayout());

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchField = new JTextField(100);
    JButton searchButton = new JButton("검색");
    JButton homeButton = new JButton("Home");
    searchPanel.add(homeButton);
    searchPanel.add(searchField);
    searchPanel.add(searchButton);
    add(searchPanel);

    // 홈 버튼 클릭 이벤트
    homeButton.addActionListener(e -> {
      searchField.setText(""); // 검색 필드 초기화
      mainApp.showSearchResults(); // 초기 화면으로 돌아가기
      mainApp.getRecommendationPanel().setVisible(true); // 추천 패널 보이기
      mainApp.getSearchResultPanel().setVisible(false); // 검색 결과 패널 숨기기
    });

    searchButton.addActionListener(e -> {
      performSearch(searchField.getText());
      ((ui.MainApp) SwingUtilities.getWindowAncestor(this)).showSearchResults(); // 검색 결과 표시
    });

    suggestionsPopup = new JPopupMenu();
    suggestionsPopup.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        searchField.requestFocusInWindow();
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        searchField.requestFocusInWindow();
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        searchField.requestFocusInWindow();
      }
    });

    searchField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (currentIndex >= 0 && currentIndex < suggestionsPopup.getComponentCount()) {
            JMenuItem selectedItem = (JMenuItem) suggestionsPopup.getComponent(currentIndex);
            searchField.setText(selectedItem.getText());
            suggestionsPopup.setVisible(false);
            performSearch(selectedItem.getText());
          } else {
            performSearch(searchField.getText());
          }
        } else {
          String query = searchField.getText();
          if (!query.isEmpty()) {
            isHighlighting = false;
            updateSuggestions(query);
          } else {
            suggestionsPopup.setVisible(false);
          }
        }
      }
    });
  }

  private void performSearch(String query) {
    List<StockInfo> stockInfos = dataStorage.loadLatestData(); // 최신 데이터 로드
    List<StockInfo> filteredResults = stockInfos.stream()
        .filter(stockInfo -> stockInfo.getIsuCd().contains(query) || stockInfo.getIsuNm().contains(query))
        .collect(Collectors.toList());

    searchResultPanel.updateResults(filteredResults); // 검색 결과 패널에 결과 전달
    mainApp.showSearchResults(); // 검색 결과 패널을 보여주는 메서드 호출
  }

  // 검색어 제안 업데이트 메서드
  private void updateSuggestions(String query) {
    suggestionsPopup.removeAll();
    currentIndex = -1;

    List<StockInfo> stockInfos = dataStorage.loadLatestData(); // 최신 데이터 로드
    List<String> suggestions = stockInfos.stream()
        .map(stockInfo -> stockInfo.getIsuCd() + " - " + stockInfo.getIsuNm())
        .filter(name -> name.contains(query))
        .distinct()
        .collect(Collectors.toList());

    for (String suggestion : suggestions) {
      JMenuItem item = new JMenuItem(suggestion);
      item.setPreferredSize(new Dimension(searchField.getWidth(), 30));
      item.addActionListener(e -> {
        searchField.setText(suggestion);
        suggestionsPopup.setVisible(false);
        performSearch(suggestion);
      });
      suggestionsPopup.add(item);
    }

    if (!suggestions.isEmpty()) {
      suggestionsPopup.show(searchField, 0, searchField.getHeight());
      suggestionsPopup.setVisible(true);
      suggestionsPopup.setPopupSize(new Dimension(searchField.getWidth(), Math.min(suggestions.size() * 100, 500)));
    } else {
      suggestionsPopup.setVisible(false);
    }
  }
}
