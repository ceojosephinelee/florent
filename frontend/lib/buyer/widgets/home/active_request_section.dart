import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/typography.dart';
import '../../providers/buyer_request_provider.dart';
import 'active_request_card.dart';

class ActiveRequestSection extends ConsumerWidget {
  const ActiveRequestSection({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncRequests = ref.watch(activeRequestsProvider);

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '진행 중인 요청',
            style: AppTypography.body(fontSize: 18, fontWeight: FontWeight.w700),
          ),
          const SizedBox(height: 16),
          asyncRequests.when(
            loading: () => const Center(
              child: Padding(
                padding: EdgeInsets.all(32),
                child: CircularProgressIndicator(color: roseColor),
              ),
            ),
            error: (e, _) => Padding(
              padding: const EdgeInsets.all(32),
              child: Text(
                '요청을 불러올 수 없습니다.',
                style: AppTypography.body(color: ink60),
              ),
            ),
            data: (requests) {
              if (requests.isEmpty) {
                return Padding(
                  padding: const EdgeInsets.symmetric(vertical: 32),
                  child: Center(
                    child: Text(
                      '아직 진행 중인 요청이 없어요.\n꽃다발 요청을 시작해 보세요!',
                      textAlign: TextAlign.center,
                      style: AppTypography.body(
                        fontSize: 14,
                        color: ink60,
                        height: 1.5,
                      ),
                    ),
                  ),
                );
              }
              return Column(
                children: requests
                    .map((r) => Padding(
                          padding: const EdgeInsets.only(bottom: 12),
                          child: ActiveRequestCard(request: r),
                        ))
                    .toList(),
              );
            },
          ),
        ],
      ),
    );
  }
}
