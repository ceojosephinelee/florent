import 'package:flutter/material.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/typography.dart';

const _sage = Color(0xFF5A7A68);

class SellerShellScreen extends StatelessWidget {
  const SellerShellScreen({
    super.key,
    required this.child,
    required this.currentIndex,
    required this.onTabChanged,
  });

  final Widget child;
  final int currentIndex;
  final ValueChanged<int> onTabChanged;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: child,
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: currentIndex,
        onTap: onTabChanged,
        backgroundColor: whiteColor,
        selectedItemColor: _sage,
        unselectedItemColor: ink30,
        type: BottomNavigationBarType.fixed,
        showUnselectedLabels: true,
        selectedLabelStyle: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600),
        unselectedLabelStyle: AppTypography.body(fontSize: 11),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home_outlined), activeIcon: Icon(Icons.home), label: '홈'),
          BottomNavigationBarItem(icon: Icon(Icons.inbox_outlined), activeIcon: Icon(Icons.inbox), label: '요청'),
          BottomNavigationBarItem(icon: Icon(Icons.bar_chart_outlined), activeIcon: Icon(Icons.bar_chart), label: '현황'),
          BottomNavigationBarItem(icon: Icon(Icons.person_outline), activeIcon: Icon(Icons.person), label: '마이'),
        ],
      ),
    );
  }
}
