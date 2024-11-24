package ui;

import calculation.StockCalculator;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.RectangleInsets;
import recommendation.RecommendationEngine;
import storage.DataStorage;
import model.StockInfo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RecommendationPanel extends JPanel {
  private RecommendationEngine recommendationEngine;
  private JPanel stocksPanel;
  private NewsPanel newsPanel;
  private DataStorage dataStorage;

  public RecommendationPanel(RecommendationEngine recommendationEngine) {
    this.recommendationEngine = recommendationEngine;
    this.dataStorage = new DataStorage();
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("AI 추천 주식", SwingConstants.LEFT);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    add(titleLabel, BorderLayout.NORTH);

    JPanel mainPanel = new JPanel(new BorderLayout());

    stocksPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    displayRecommendedStocks();
    mainPanel.add(stocksPanel, BorderLayout.CENTER);

    newsPanel = new NewsPanel();
    mainPanel.add(newsPanel, BorderLayout.SOUTH);

    add(mainPanel, BorderLayout.CENTER);
  }

  private void displayRecommendedStocks() {
    List<StockInfo> recommendedStocks = recommendationEngine.recommendStocks();

    if (recommendedStocks.isEmpty()) {
      stocksPanel.add(new JLabel("추천할 종목이 없습니다."));
    } else {
      for (StockInfo stock : recommendedStocks) {
        JPanel stockPanel = createStockPanel(stock);
        stocksPanel.add(stockPanel);
      }
    }

    // 뉴스 패널 하단 공백 크기 제거
    stocksPanel.setPreferredSize(new Dimension(stocksPanel.getWidth(), 300));  // 최소 높이 설정
  }

  private JPanel createStockPanel(StockInfo stock) {
    JPanel stockPanel = new JPanel(new BorderLayout());
    String stockText = String.format("<html><b>종목명: %s</b></html>", stock.getIsuNm());
    JLabel stockLabel = new JLabel(stockText, SwingConstants.CENTER);  // 종목명 중앙 정렬
    stockLabel.setFont(new Font("Arial", Font.BOLD, 14));  // 폰트 크기 조정 (선택적)

    JPanel graphPanel = createGraphPanel(stock.getIsuCd());

    // 그래프 아래의 값들을 BoxLayout을 사용하여 세로로 나열하고, 간격 추가
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));  // 세로로 나열
    detailsPanel.add(Box.createVerticalStrut(5));  // 상단 여백 추가

    JLabel flucRateLabel = new JLabel(String.format("3개월 평균 변동률: %.2f", StockCalculator.safeParseDouble(stock.getFlucRt())));
    JLabel volumeLabel = new JLabel(String.format("3개월 평균 거래량: %.2f", StockCalculator.safeParseDouble(stock.getAccTrdVol())));
    JLabel marketCapLabel = new JLabel(String.format("최대시가총액: %, .2f", StockCalculator.safeParseDouble(stock.getMktCap())));

    detailsPanel.add(flucRateLabel);
    detailsPanel.add(Box.createVerticalStrut(10));  // 항목 간의 간격 추가
    detailsPanel.add(volumeLabel);
    detailsPanel.add(Box.createVerticalStrut(10));  // 항목 간의 간격 추가
    detailsPanel.add(marketCapLabel);
    detailsPanel.add(Box.createVerticalStrut(5));  // 하단 여백 추가

    stockPanel.add(stockLabel, BorderLayout.NORTH);
    stockPanel.add(graphPanel, BorderLayout.CENTER);
    stockPanel.add(detailsPanel, BorderLayout.SOUTH);  // 그래프 아래에 값들 표시

    return stockPanel;
  }

  private JPanel createGraphPanel(String isuCd) {
    JPanel graphPanel = new JPanel(new BorderLayout());

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    List<StockInfo> threeMonthData = dataStorage.loadOrCollectThreeMonthData();

    double minPrice = Double.MAX_VALUE;
    double maxPrice = Double.MIN_VALUE;

    for (StockInfo stockInfo : threeMonthData) {
      if (stockInfo.getIsuCd().equals(isuCd)) {
        String date = stockInfo.getBasDd();
        double closingPrice = Double.parseDouble(stockInfo.getTddClsPrc());
        dataset.addValue(closingPrice, "가격", date);

        // 최소값과 최대값을 추적하여 Y축 범위 설정
        if (closingPrice < minPrice) {
          minPrice = closingPrice;
        }
        if (closingPrice > maxPrice) {
          maxPrice = closingPrice;
        }
      }
    }

    // 가격이 0보다 낮으면 0으로 설정
    minPrice = Math.max(minPrice, 0);

    JFreeChart chart = ChartFactory.createLineChart(
        null,                   // 차트 제목
        "날짜",                  // X축 레이블
        "가격",                  // Y축 레이블
        dataset                // 데이터셋
    );

    // 그래프의 Padding을 RectangleInsets로 설정 (하단 여백을 0으로 설정)
    chart.setPadding(new RectangleInsets(0, 0, 0, 0));  // 상단, 왼쪽, 하단, 오른쪽 여백을 조정 (하단 여백 0으로 설정)
    CategoryPlot plot = (CategoryPlot) chart.getPlot();

    // Y축 설정: 최소값과 최대값 설정
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setRange(minPrice, maxPrice);  // Y축 범위 수동 설정

    // 자동 Y축 범위 조정 비활성화
    rangeAxis.setAutoRange(false);

    // 하단 축 숨기기
    plot.getDomainAxis().setVisible(false);

    // 그래프 하단 50% 제거 효과를 위해 영역 크기 조절
    plot.setDomainGridlinesVisible(false);  // 그리드라인을 숨길 수 있습니다. 필요에 따라 수정

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(200, 150));  // 그래프의 크기 조정
    chartPanel.setMaximumSize(new Dimension(200, 150));  // 최대 크기를 지정하여 크기 제한
    graphPanel.add(chartPanel, BorderLayout.CENTER);

    return graphPanel;
  }
}