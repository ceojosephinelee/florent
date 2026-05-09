import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/request_form_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/bottom_cta_button.dart';
import '../widgets/common/custom_tag_input.dart';
import '../widgets/common/step_progress_bar.dart';
import '../widgets/common/tag_chip.dart';

class RequestStep1Screen extends ConsumerWidget {
  const RequestStep1Screen({super.key});

  static const _purposeOptions = [
    '🎂 생일',
    '💍 기념일',
    '💝 프로포즈',
    '🎓 졸업',
    '🕊 장례',
    '💼 비즈니스',
  ];

  static const _relationOptions = [
    '💑 연인',
    '👨‍👩‍👧 부모님',
    '👫 친구',
    '🙋 본인',
    '👔 비즈니스',
  ];

  static const _moodOptions = [
    '🌹 로맨틱',
    '🌿 자연스러운',
    '✨ 고급스러운',
    '🌸 청순한',
    '🎀 귀여운',
    '🖤 모던',
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(requestFormProvider);
    final notifier = ref.read(requestFormProvider.notifier);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '꽃 요청하기'),
            const StepProgressBar(totalSteps: 4, currentStep: 1),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _SectionLabel(emoji: '🎯', label: '목적', required: true),
                    const SizedBox(height: 10),
                    _TagGrid(
                      presetOptions: _purposeOptions,
                      selected: form.purposeTags,
                      onToggle: notifier.togglePurposeTag,
                    ),
                    const SizedBox(height: 8),
                    CustomTagInput(
                      placeholder: '직접 입력하기',
                      onSubmit: notifier.togglePurposeTag,
                    ),
                    const SizedBox(height: 24),
                    _SectionLabel(emoji: '👥', label: '관계', required: true),
                    const SizedBox(height: 10),
                    _TagGrid(
                      presetOptions: _relationOptions,
                      selected: form.relationTags,
                      onToggle: notifier.toggleRelationTag,
                    ),
                    const SizedBox(height: 8),
                    CustomTagInput(
                      placeholder: '직접 입력하기',
                      onSubmit: notifier.toggleRelationTag,
                    ),
                    const SizedBox(height: 24),
                    _SectionLabel(
                      emoji: '✨',
                      label: '분위기',
                      suffix: '(복수 선택 가능)',
                    ),
                    const SizedBox(height: 10),
                    _TagGrid(
                      presetOptions: _moodOptions,
                      selected: form.moodTags,
                      onToggle: notifier.toggleMoodTag,
                    ),
                    const SizedBox(height: 8),
                    CustomTagInput(
                      placeholder: '원하는 분위기 직접 입력',
                      onSubmit: notifier.toggleMoodTag,
                    ),
                    const SizedBox(height: 24),
                    _SectionLabel(emoji: '📝', label: '추가 요청사항'),
                    const SizedBox(height: 6),
                    Text(
                      '선택 사항이에요. 플로리스트에게 전달할 내용을 자유롭게 적어주세요.',
                      style: AppTypography.body(fontSize: 10, color: ink30),
                    ),
                    const SizedBox(height: 10),
                    Container(
                      decoration: BoxDecoration(
                        color: whiteColor,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: borderColor, width: 1.5),
                      ),
                      child: TextField(
                        maxLines: 4,
                        maxLength: 500,
                        style: AppTypography.body(fontSize: 13),
                        decoration: InputDecoration(
                          hintText: '예: 알러지가 있어요 / 특정 색상 제외 / 포장 방식 요청',
                          hintStyle: AppTypography.body(fontSize: 13, color: ink30),
                          border: InputBorder.none,
                          contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                          counterStyle: AppTypography.body(fontSize: 10, color: ink30),
                        ),
                        onChanged: notifier.setAdditionalNotes,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            BottomCtaButton(
              label: '다음 — 예산 선택',
              enabled: form.isStep1Valid,
              onPressed: () => context.push('/buyer/request/step2'),
            ),
          ],
        ),
      ),
    );
  }
}

class _SectionLabel extends StatelessWidget {
  const _SectionLabel({
    required this.emoji,
    required this.label,
    this.required = false,
    this.suffix,
  });

  final String emoji;
  final String label;
  final bool required;
  final String? suffix;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text(
          '$emoji $label',
          style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600),
        ),
        if (required)
          Text(' *', style: TextStyle(color: roseColor, fontSize: 14)),
        if (suffix != null) ...[
          const SizedBox(width: 6),
          Text(
            suffix!,
            style: AppTypography.body(fontSize: 10, color: ink30),
          ),
        ],
      ],
    );
  }
}

class _TagGrid extends StatelessWidget {
  const _TagGrid({
    required this.presetOptions,
    required this.selected,
    required this.onToggle,
  });

  final List<String> presetOptions;
  final List<String> selected;
  final void Function(String) onToggle;

  @override
  Widget build(BuildContext context) {
    // 프리셋 + 직접 입력한 커스텀 태그를 합쳐서 표시
    final customTags =
        selected.where((t) => !presetOptions.contains(t)).toList();
    final allTags = [...presetOptions, ...customTags];

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: allTags
          .map((tag) => TagChip(
                label: tag,
                isSelected: selected.contains(tag),
                onTap: () => onToggle(tag),
              ))
          .toList(),
    );
  }
}
