import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/typography.dart';
import '../../providers/buyer_home_provider.dart';

class BuyerTopBar extends ConsumerWidget {
  const BuyerTopBar({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final unreadCount = ref.watch(unreadNotificationCountProvider);

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            'Florent',
            style: AppTypography.serif(fontSize: 28, fontWeight: FontWeight.w700),
          ),
          GestureDetector(
            onTap: () => context.push('/buyer/notifications'),
            child: Stack(
              children: [
                const Icon(Icons.notifications_outlined, size: 28, color: inkColor),
                if (unreadCount > 0)
                  Positioned(
                    right: 0,
                    top: 0,
                    child: Container(
                      width: 16,
                      height: 16,
                      decoration: const BoxDecoration(
                        color: roseColor,
                        shape: BoxShape.circle,
                      ),
                      alignment: Alignment.center,
                      child: Text(
                        '$unreadCount',
                        style: AppTypography.mono(fontSize: 9, color: whiteColor, fontWeight: FontWeight.w600),
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
