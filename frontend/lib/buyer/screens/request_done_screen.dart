import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/models/enums.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/request_form_provider.dart';
import '../widgets/common/expiry_timer.dart';

class RequestDoneScreen extends ConsumerWidget {
  const RequestDoneScreen({super.key});

  String _cleanTag(String tag) {
    return tag.replaceAll(RegExp(r'[^\w가-힣\s]'), '').trim();
  }

  String _formatTagList(List<String> tags) {
    if (tags.isEmpty) return '-';
    return tags.map(_cleanTag).join(' · ');
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(requestFormProvider);

    final budgetTier = BudgetTier.values
        .where((t) => t.value == form.budgetTier)
        .firstOrNull;
    final budgetLabel = budgetTier != null
        ? '${budgetTier.label} (${budgetTier.priceRange})'
        : '-';

    final fulfillmentLabel = form.isPickup ? '픽업' : '배송';
    String dateDisplay = '-';
    if (form.fulfillmentDate != null) {
      final date = DateTime.parse(form.fulfillmentDate!);
      final weekday = ['월', '화', '수', '목', '금', '토', '일'];
      dateDisplay =
          '${date.year}년 ${date.month}월 ${date.day}일 (${weekday[date.weekday - 1]})';
    }

    final timeLabel = form.isPickup ? '픽업 시간' : '배송 시간';

    final expiresAt = DateTime.now().add(const Duration(hours: 48));

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
          child: Column(
            children: [
              const SizedBox(height: 24),
              Container(
                width: 72,
                height: 72,
                decoration: BoxDecoration(
                  color: roseColor,
                  borderRadius: kBorderRadiusLg,
                ),
                alignment: Alignment.center,
                child: const Text('🌷', style: TextStyle(fontSize: 32)),
              ),
              const SizedBox(height: 14),
              Text(
                '요청이 전달됐어요',
                style: AppTypography.serif(
                  fontSize: 24,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 10),
              Text(
                '반경 2km 내 플로리스트에게\n요청서가 전송됐습니다\n제안서가 도착하면 바로 알려드릴게요 🔔',
                textAlign: TextAlign.center,
                style: AppTypography.body(
                  fontSize: 12,
                  color: ink60,
                  height: 1.7,
                ),
              ),
              const SizedBox(height: 24),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: whiteColor,
                  borderRadius: kBorderRadiusLg,
                  border: Border.all(color: borderColor, width: 1.5),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _InfoRow(
                      label: '목적',
                      value: _formatTagList(form.purposeTags),
                    ),
                    const _Divider(),
                    _InfoRow(
                      label: '관계',
                      value: _formatTagList(form.relationTags),
                    ),
                    const _Divider(),
                    _InfoRow(
                      label: '분위기',
                      value: form.moodTags.isNotEmpty
                          ? _formatTagList(form.moodTags)
                          : '-',
                    ),
                    const _Divider(),
                    _InfoRow(label: '예산', value: budgetLabel),
                    const _Divider(),
                    _InfoRow(label: '수령 방법', value: fulfillmentLabel),
                    const _Divider(),
                    _InfoRow(label: '수령 날짜', value: dateDisplay),
                    const _Divider(),
                    _InfoColumn(
                      label: timeLabel,
                      values: form.requestedTimeSlots.map((s) {
                        if (form.isPickup) return s.value;
                        switch (s.value) {
                          case 'MORNING':
                            return '오전 (9-12)';
                          case 'AFTERNOON':
                            return '오후 (12-18)';
                          case 'EVENING':
                            return '저녁 (18-21)';
                          default:
                            return s.value;
                        }
                      }).toList(),
                    ),
                    const _Divider(),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          '요청 만료',
                          style: AppTypography.body(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: ink60,
                          ),
                        ),
                        Row(
                          children: [
                            const Text('⏱ ', style: TextStyle(fontSize: 12)),
                            ExpiryTimer(expiresAt: expiresAt),
                          ],
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity,
                height: 48,
                child: ElevatedButton(
                  onPressed: () {
                    ref.read(requestFormProvider.notifier).reset();
                    context.go('/buyer/home');
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: roseColor,
                    foregroundColor: whiteColor,
                    shape: RoundedRectangleBorder(
                      borderRadius: kBorderRadiusLg,
                    ),
                    elevation: 0,
                  ),
                  child: Text(
                    '홈으로 돌아가기 →',
                    style: AppTypography.body(
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                      color: whiteColor,
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  const _InfoRow({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 70,
            child: Text(
              label,
              style: AppTypography.body(
                fontSize: 12,
                fontWeight: FontWeight.w600,
                color: ink60,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: AppTypography.body(fontSize: 12),
              textAlign: TextAlign.right,
            ),
          ),
        ],
      ),
    );
  }
}

class _InfoColumn extends StatelessWidget {
  const _InfoColumn({required this.label, required this.values});

  final String label;
  final List<String> values;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 70,
            child: Text(
              label,
              style: AppTypography.body(
                fontSize: 12,
                fontWeight: FontWeight.w600,
                color: ink60,
              ),
            ),
          ),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: values
                  .map((v) => Padding(
                        padding: const EdgeInsets.only(bottom: 2),
                        child: Text(
                          v,
                          style: AppTypography.mono(fontSize: 12),
                        ),
                      ))
                  .toList(),
            ),
          ),
        ],
      ),
    );
  }
}

class _Divider extends StatelessWidget {
  const _Divider();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Divider(height: 1, color: borderColor.withValues(alpha: 0.5)),
    );
  }
}
