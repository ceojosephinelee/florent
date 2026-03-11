import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';
import '../../providers/buyer_home_provider.dart';

class GreetingSection extends ConsumerWidget {
  const GreetingSection({super.key, this.onCreateRequest});

  final VoidCallback? onCreateRequest;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final name = ref.watch(buyerNameProvider);

    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 8, 20, 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '$name님,\n어떤 꽃을 찾고 계세요?',
            style: AppTypography.body(
              fontSize: 22,
              fontWeight: FontWeight.w700,
              height: 1.4,
            ),
          ),
          const SizedBox(height: 20),
          SizedBox(
            width: double.infinity,
            height: 52,
            child: ElevatedButton(
              onPressed: onCreateRequest,
              style: ElevatedButton.styleFrom(
                backgroundColor: roseColor,
                foregroundColor: whiteColor,
                shape: RoundedRectangleBorder(
                  borderRadius: kBorderRadiusMd,
                ),
                elevation: 0,
              ),
              child: Text(
                '꽃다발 요청하기',
                style: AppTypography.body(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: whiteColor,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
