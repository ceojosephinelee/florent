import 'package:flutter/material.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/typography.dart';

class BuyerShellScreen extends StatelessWidget {
  const BuyerShellScreen({
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
        selectedItemColor: roseColor,
        unselectedItemColor: ink30,
        type: BottomNavigationBarType.fixed,
        showUnselectedLabels: true,
        selectedLabelStyle: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600),
        unselectedLabelStyle: AppTypography.body(fontSize: 11),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home_outlined), activeIcon: Icon(Icons.home), label: '홈'),
          BottomNavigationBarItem(icon: Icon(Icons.receipt_long_outlined), activeIcon: Icon(Icons.receipt_long), label: '내 요청'),
          BottomNavigationBarItem(icon: Icon(Icons.calendar_today_outlined), activeIcon: Icon(Icons.calendar_today), label: '예약'),
          BottomNavigationBarItem(icon: Icon(Icons.person_outline), activeIcon: Icon(Icons.person), label: '마이'),
        ],
      ),
    );
  }
}
