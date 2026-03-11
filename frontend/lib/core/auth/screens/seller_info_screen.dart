import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../theme/colors.dart';
import '../../theme/radius.dart';
import '../../theme/typography.dart';
import '../auth_provider.dart';

const _sage = Color(0xFF5A7A68);

class SellerInfoScreen extends ConsumerStatefulWidget {
  const SellerInfoScreen({super.key});

  @override
  ConsumerState<SellerInfoScreen> createState() => _SellerInfoScreenState();
}

class _SellerInfoScreenState extends ConsumerState<SellerInfoScreen> {
  final _shopNameController = TextEditingController();
  final _businessNumberController = TextEditingController();
  String? _address = '서울 강남구 역삼동 123-45'; // mock 기본값

  bool get _isValid => _shopNameController.text.trim().isNotEmpty;

  @override
  void dispose() {
    _shopNameController.dispose();
    _businessNumberController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = ref.watch(authProvider);

    ref.listen<AuthState>(authProvider, (prev, next) {
      if (!next.isLoading && next.status == AuthStatus.sellerAuthenticated) {
        context.go('/seller/home');
      }
    });

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '가게 정보 입력',
                      style: AppTypography.serif(fontSize: 24, fontWeight: FontWeight.w600),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      '판매를 시작하기 위한 기본 정보를 입력해주세요.',
                      style: AppTypography.body(fontSize: 13, color: ink60),
                    ),
                    const SizedBox(height: 28),

                    // 가게 이름
                    _label('🏪 가게 이름 *'),
                    const SizedBox(height: 6),
                    _textField(
                      controller: _shopNameController,
                      hint: '예) 꽃피는 봄날',
                      onChanged: (_) => setState(() {}),
                    ),

                    const SizedBox(height: 20),

                    // 가게 주소
                    _label('📍 가게 주소 *'),
                    const SizedBox(height: 6),
                    GestureDetector(
                      onTap: () {
                        // Mock: 카카오 주소 검색 API 연동 예정
                        setState(() => _address = '서울 강남구 역삼동 123-45');
                      },
                      child: Container(
                        width: double.infinity,
                        padding: const EdgeInsets.all(14),
                        decoration: BoxDecoration(
                          color: whiteColor,
                          borderRadius: kBorderRadiusSm,
                          border: Border.all(color: borderColor, width: 1.5),
                        ),
                        child: Row(
                          children: [
                            Expanded(
                              child: Text(
                                _address ?? '주소를 검색해주세요',
                                style: AppTypography.body(
                                  fontSize: 13,
                                  color: _address != null ? inkColor : ink30,
                                ),
                              ),
                            ),
                            Icon(Icons.search, size: 18, color: ink30),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 6),

                    // 지도 미리보기 placeholder
                    Container(
                      width: double.infinity,
                      height: 120,
                      decoration: BoxDecoration(
                        color: sageLt,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: borderColor),
                      ),
                      alignment: Alignment.center,
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Text('📍', style: TextStyle(fontSize: 24)),
                          const SizedBox(height: 4),
                          Text('지도 미리보기', style: AppTypography.body(fontSize: 11, color: ink60)),
                          Text('(카카오맵 연동 예정)', style: AppTypography.body(fontSize: 10, color: ink30)),
                        ],
                      ),
                    ),

                    const SizedBox(height: 20),

                    // 사업자등록번호
                    _label('📋 사업자등록번호 (선택)'),
                    const SizedBox(height: 6),
                    _textField(
                      controller: _businessNumberController,
                      hint: '000-00-00000',
                      keyboardType: TextInputType.number,
                    ),
                    const SizedBox(height: 6),
                    Text(
                      '없어도 서비스 이용이 가능해요.',
                      style: AppTypography.body(fontSize: 10, color: ink30),
                    ),
                  ],
                ),
              ),
            ),

            // CTA 버튼
            SafeArea(
              top: false,
              child: Padding(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 12),
                child: SizedBox(
                  width: double.infinity,
                  height: 52,
                  child: ElevatedButton(
                    onPressed: (_isValid && !auth.isLoading)
                        ? () => ref.read(authProvider.notifier).registerSellerInfo(
                              shopName: _shopNameController.text.trim(),
                              shopAddress: _address ?? '',
                              shopLat: 37.501286,
                              shopLng: 127.039583,
                              businessNumber: _businessNumberController.text.trim().isEmpty
                                  ? null
                                  : _businessNumberController.text.trim(),
                            )
                        : null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: _sage,
                      disabledBackgroundColor: ink30,
                      foregroundColor: whiteColor,
                      shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd),
                      elevation: 0,
                    ),
                    child: Text(
                      auth.isLoading ? '등록 중...' : '시작하기',
                      style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: whiteColor),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _label(String text) =>
      Text(text, style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600));

  Widget _textField({
    required TextEditingController controller,
    required String hint,
    TextInputType? keyboardType,
    ValueChanged<String>? onChanged,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: whiteColor,
        borderRadius: kBorderRadiusSm,
        border: Border.all(color: borderColor, width: 1.5),
      ),
      child: TextField(
        controller: controller,
        keyboardType: keyboardType,
        style: AppTypography.body(fontSize: 13),
        decoration: InputDecoration(
          hintText: hint,
          hintStyle: AppTypography.body(fontSize: 13, color: ink30),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        onChanged: onChanged,
      ),
    );
  }
}
