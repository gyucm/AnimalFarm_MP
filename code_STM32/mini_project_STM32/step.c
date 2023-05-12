/******************************************************************************
  ��Ʈ ����:
    1)  ARM Cortex-M4 ����� ��ƮC�� 0~3������(PC0~PC3) 4�� ���̺��
        Step Motor����� ST_A,ST_B,ST_A/,ST_B/ �ɿ� �����Ѵ�.
******************************************************************************/
// stm32f4xx�� �� �������͵��� ������ �������
#include "stm32f4xx.h"
#include "step.h"

// delay �Լ�
static void Delay(const uint32_t Count)
{
  __IO uint32_t index = 0; 
  for(index = (16800 * Count); index != 0; index--);
}


void Step_Init()
{
  GPIO_InitTypeDef   GPIO_InitStructure;

  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOC, ENABLE);

  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_OUT;
  GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_100MHz;
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_8|GPIO_Pin_8|GPIO_Pin_10|GPIO_Pin_11;
  GPIO_Init(GPIOC, &GPIO_InitStructure);
}
void Moter_ON()
{
  GPIO_Write(GPIOC, 0x01);
  Delay(10);

  GPIO_Write(GPIOC, 0x02);
  Delay(10);

  GPIO_Write(GPIOC, 0x04);
  Delay(10);

  GPIO_Write(GPIOC, 0x08);
  Delay(10);
}

