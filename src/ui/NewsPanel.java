package ui;

import news.NewsFetcher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

public class NewsPanel extends JPanel {
  private DefaultListModel<String> newsListModel;
  private JList<String> newsList;
  private List<String> newsUrls;

  public NewsPanel() {
    setLayout(new BorderLayout());
    newsListModel = new DefaultListModel<>();
    newsList = new JList<>(newsListModel);
    newsList.setCellRenderer(new NewsCellRenderer());  // 커스텀 렌더러 설정

    newsList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int index = newsList.locationToIndex(e.getPoint());
          openWebPage(newsUrls.get(index));
        }
      }
    });

    add(new JScrollPane(newsList), BorderLayout.CENTER);
    loadNews("주식");
  }

  // 뉴스 로드 메서드
  public void loadNews(String query) {
    NewsFetcher newsFetcher = new NewsFetcher();
    List<NewsFetcher.NewsItem> newsItems = newsFetcher.fetchNews(query);

    for (NewsFetcher.NewsItem item : newsItems) {
      String title = "<html><b style='font-size: 22px;'>" + item.getTitle() + "</b><br>";
      String summary = "<p style='font-size: 16px; line-height: 1.5;'>" + item.getSummary() + "</p></html>";
      newsListModel.addElement(title + summary);
    }
    newsUrls = newsFetcher.getNewsUrls();
  }

  // 웹 페이지 열기
  private void openWebPage(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 커스텀 셀 렌더러
  static class NewsCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      label.setVerticalAlignment(SwingConstants.TOP);
      label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 기사 간 여백 추가
      return label;
    }
  }
}
