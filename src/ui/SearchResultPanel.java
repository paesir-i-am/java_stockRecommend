package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import storage.DataStorage;
import model.StockInfo;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class SearchResultPanel extends JPanel {
  private JTextArea resultArea;
  private JPanel graphPanel; // 그래프를 표시할 패널
  private List<StockInfo> currentResults;
  private int currentPage = 1;
  private static final int ITEMS_PER_PAGE = 1;

  private final DataStorage dataStorage = new DataStorage(); // 데이터 저장소 인스턴스 생성

  public SearchResultPanel() {
    setLayout(new BorderLayout());

    resultArea = new JTextArea();
    resultArea.setEditable(false);
    resultArea.setLineWrap(true);
    resultArea.setWrapStyleWord(true);

    // 기존 폰트 크기만 변경
    Font currentFont = resultArea.getFont(); // 기존 폰트 가져오기
    Font newFont = currentFont.deriveFont(18f); // 기존 폰트 크기에서 크기만 변경 (18f는 새 크기)
    resultArea.setFont(newFont); // 폰트 크기 적용

    add(new JScrollPane(resultArea), BorderLayout.CENTER);

    graphPanel = new JPanel(new BorderLayout());
    add(graphPanel, BorderLayout.EAST); // 그래프 패널을 오른쪽에 배치

    JButton prevButton = new JButton("이전");
    JButton nextButton = new JButton("다음");

    prevButton.addActionListener(e -> {
      if (currentPage > 1) {
        currentPage--;
        displayPage(currentResults);
      }
    });

    nextButton.addActionListener(e -> {
      if (currentPage < currentResults.size()) {
        currentPage++;
        displayPage(currentResults);
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(prevButton);
    buttonPanel.add(nextButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  public void updateResults(List<StockInfo> results) {
    currentResults = results;
    currentPage = 1;
    displayPage(results);
  }

  private void displayPage(List<StockInfo> results) {
    int start = (currentPage - 1) * ITEMS_PER_PAGE;
    if (start < results.size()) {
      StockInfo stockInfo = results.get(start);

      // 종목 정보를 JTextArea에 표시
      StringBuilder stockResults = new StringBuilder();
      stockResults
          .append("기준 일자: ").append(stockInfo.getBasDd()).append("\n\n\n")
          .append("종목 코드: ").append(stockInfo.getIsuCd()).append("\n\n\n")
          .append("종목 이름: ").append(stockInfo.getIsuNm()).append("\n\n\n")
          .append("시장 구분: ").append(stockInfo.getMktNm()).append("\n\n\n")
          .append("시가: ").append(formatNumberWithComma(Double.parseDouble(stockInfo.getTddOpnPrc()))).append("\n\n\n")
          .append("고가: ").append(formatNumberWithComma(Double.parseDouble(stockInfo.getTddHgPrc()))).append("\n\n\n")
          .append("저가: ").append(formatNumberWithComma(Double.parseDouble(stockInfo.getTddLwPrc()))).append("\n\n\n")
          .append("등락률: ").append(stockInfo.getFlucRt()).append(" %").append("\n\n\n\n");

      resultArea.setText(stockResults.toString()); // JTextArea에 표시

      // 그래프 표시
      displayGraph(stockInfo.getIsuCd());
    }
  }

  private void displayGraph(String isuCd) {
    List<StockInfo> threeMonthData = dataStorage.loadOrCollectThreeMonthData(); // 3개월 데이터 로드
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    // 특정 종목에 대한 데이터만 필터링하여 그래프에 추가
    String chartTitle = null;
    for (StockInfo stockInfo : threeMonthData) {
      if (stockInfo.getIsuCd().equals(isuCd)) {
        if (chartTitle == null) {
          chartTitle = stockInfo.getIsuNm();
        }
        String date = stockInfo.getBasDd(); // 기준 일자
        double closingPrice = Double.parseDouble(stockInfo.getTddClsPrc()); // 종가
        dataset.addValue(closingPrice, "가격", date);
      }
    }

    JFreeChart chart = ChartFactory.createLineChart(
        chartTitle != null ? chartTitle + "  3개월 가격 변동" : "  3개월 가격 변동",
        "날짜",
        "가격",
        dataset
    );

    // 기존 그래프 제거 및 새 그래프 추가
    graphPanel.removeAll();
    graphPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
    graphPanel.revalidate();
    graphPanel.repaint();
  }

  // 숫자에 세자리마다 쉼표 추가
  private String formatNumberWithComma(double number) {
    DecimalFormat formatter = new DecimalFormat("#,###");
    return formatter.format(number);
  }
}
