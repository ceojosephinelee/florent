import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../buyer/widgets/common/app_nav_bar.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerProposalStep1Screen extends ConsumerWidget {
  const SellerProposalStep1Screen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(sellerProposalFormProvider);
    final notifier = ref.read(sellerProposalFormProvider.notifier);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '제안서 작성'),
            const _SellerStepBar(currentStep: 1),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(color: _sageLt, borderRadius: kBorderRadiusSm, border: Border.all(color: const Color(0xFFB8CFC4))),
                      child: Text('📋 구매자 요청: 생일 · 부모님 · 로맨틱·자연스러운 · 기본형(6~8만원)', style: AppTypography.body(fontSize: 11, color: _sage)),
                    ),
                    const SizedBox(height: 20),
                    _label('🌸 꽃다발 이름 *'),
                    const SizedBox(height: 6),
                    _textField(
                      hint: '예) 핑크 작약으로 물든 봄날의 꽃다발',
                      onChanged: notifier.setConceptTitle,
                    ),
                    Text('구매자 목록에 이 이름이 노출돼요', style: AppTypography.body(fontSize: 10, color: ink30)),
                    const SizedBox(height: 20),
                    _label('📝 꽃 구성 설명 *'),
                    const SizedBox(height: 6),
                    _qaBlock('🌹 메인 꽃', '핑크 장미 5송이, 작약 3송이', notifier.setMainFlowers),
                    _qaBlock('🌿 서브 꽃·그린', '유칼립투스, 스타티스', notifier.setSubFlowers),
                    _qaBlock('🎨 전체 컨셉·색감', '따뜻한 봄 핑크톤, 로맨틱하고 풍성한 느낌', notifier.setConcept),
                    _qaBlock('🎀 포장·마무리', '크림색 새틴 리본, 크라프트지 포장', notifier.setWrapping),
                    _qaBlock('💌 추천 이유', '어머니께 드리기 딱 좋은 따뜻한 컬러예요.', notifier.setRecommendation),
                    const SizedBox(height: 20),
                    _label('💰 제안 가격 *'),
                    const SizedBox(height: 6),
                    _textField(
                      hint: '직접 입력 (원)',
                      keyboardType: TextInputType.number,
                      onChanged: (v) => notifier.setPrice(int.tryParse(v.replaceAll(',', '')) ?? 0),
                    ),
                    const SizedBox(height: 6),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(color: creamColor, borderRadius: kBorderRadiusSm, border: Border.all(color: borderColor)),
                      child: Text('💡 구매자 예산 참고 범위는 6~8만원이에요. 범위를 크게 벗어나면 선택률이 낮아질 수 있어요.', style: AppTypography.body(fontSize: 10, color: ink60)),
                    ),
                    const SizedBox(height: 20),
                    _label('📷 사진 첨부 (선택)'),
                    const SizedBox(height: 6),
                    Container(
                      width: 60, height: 60,
                      decoration: BoxDecoration(color: creamColor, borderRadius: kBorderRadiusSm, border: Border.all(color: borderColor, width: 1.5)),
                      alignment: Alignment.center,
                      child: Text('+', style: AppTypography.body(fontSize: 24, color: ink30)),
                    ),
                    const SizedBox(height: 4),
                    Text('레퍼런스나 직접 제작한 예시 이미지를 올려주세요.', style: AppTypography.body(fontSize: 10, color: ink30)),
                  ],
                ),
              ),
            ),
            SafeArea(
              top: false,
              child: Padding(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 12),
                child: SizedBox(
                  width: double.infinity,
                  height: 52,
                  child: ElevatedButton(
                    onPressed: form.isStep1Valid ? () => context.push('/seller/proposals/new/step2/pickup') : null,
                    style: ElevatedButton.styleFrom(backgroundColor: _sage, disabledBackgroundColor: ink30, foregroundColor: whiteColor, shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd), elevation: 0),
                    child: Text('다음 — 시간 선택 →', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: whiteColor)),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _label(String text) => Text(text, style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600));

  Widget _textField({required String hint, TextInputType? keyboardType, required ValueChanged<String> onChanged}) {
    return Container(
      decoration: BoxDecoration(color: creamColor, borderRadius: kBorderRadiusSm, border: Border.all(color: borderColor, width: 1.5)),
      child: TextField(
        keyboardType: keyboardType,
        style: AppTypography.body(fontSize: 13),
        decoration: InputDecoration(hintText: hint, hintStyle: AppTypography.body(fontSize: 13, color: ink30), border: InputBorder.none, contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12)),
        onChanged: onChanged,
      ),
    );
  }

  Widget _qaBlock(String question, String hint, ValueChanged<String> onChanged) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusSm, border: Border.all(color: borderColor)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(question, style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
            const SizedBox(height: 4),
            TextField(
              style: AppTypography.body(fontSize: 12),
              decoration: InputDecoration(hintText: hint, hintStyle: AppTypography.body(fontSize: 12, color: ink30), border: InputBorder.none, isDense: true, contentPadding: EdgeInsets.zero),
              onChanged: onChanged,
            ),
          ],
        ),
      ),
    );
  }
}

class _SellerStepBar extends StatelessWidget {
  const _SellerStepBar({required this.currentStep});
  final int currentStep;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      child: Row(
        children: [
          _dot(1, currentStep),
          Expanded(child: Container(height: 2, color: currentStep > 1 ? _sage : borderColor)),
          _dot(2, currentStep),
        ],
      ),
    );
  }

  Widget _dot(int step, int current) {
    final isDone = step < current;
    final isActive = step == current;
    Color bg = isDone ? _sage : isActive ? inkColor : borderColor;
    Widget child = isDone
        ? const Icon(Icons.check, size: 14, color: whiteColor)
        : Text('$step', style: AppTypography.mono(fontSize: 11, color: whiteColor));
    return Container(width: 28, height: 28, decoration: BoxDecoration(shape: BoxShape.circle, color: bg), alignment: Alignment.center, child: child);
  }
}
